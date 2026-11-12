import base64
import hashlib
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
import zipfile

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from configure_build import append_environment, memory_profile, signing_environment
from package_release import package_release
from publish_release import publish

COMMIT = "a" * 40


class BuildToolsTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)

    def fixture(self):
        source = self.root / "apk"
        source.mkdir(exist_ok=True)
        with zipfile.ZipFile(source / "app-release.apk", "w") as archive:
            archive.writestr("AndroidManifest.xml", b"fixture manifest")
            archive.writestr("classes.dex", b"fixture dex")
        metadata = {"elements": [{"outputFile": "app-release.apk", "versionCode": 42,
                                  "versionName": "0.1.0-build.42", "filters": []}]}
        (source / "output-metadata.json").write_text(json.dumps(metadata))
        return source, metadata

    def packaged(self):
        source, _ = self.fixture()
        return package_release(source, self.root / "distribution", COMMIT, "42", "fallback")

    def test_large_runner_gets_six_gib_gradle_and_two_gib_kotlin(self):
        for memory in (15872, 16000, 16384):
            p = memory_profile(memory, 16)
            self.assertEqual((6144, 2048, 4), (p["gradle_mib"], p["kotlin_mib"], p["workers"]))

    def test_smaller_runners_keep_room_for_the_operating_system(self):
        for memory in (4096, 7168, 10240):
            p = memory_profile(memory, 2)
            total = sum(p[k] for k in p if k.endswith("_mib"))
            self.assertLessEqual(total, memory - 1024)
            self.assertEqual(2, p["workers"])

    def test_no_secrets_selects_the_stable_fallback(self):
        self.assertEqual({"SERTUM_SIGNING_MODE": "fallback"}, signing_environment({}, self.root))
        self.assertEqual([], list(self.root.iterdir()))

    def test_partial_private_credentials_fail_without_writing_a_key(self):
        with self.assertRaises(ValueError):
            signing_environment({"ANDROID_KEYSTORE_BASE64": "not-a-key"}, self.root)
        self.assertEqual([], list(self.root.iterdir()))

    def test_private_signing_creates_a_restricted_temporary_file(self):
        values = {"ANDROID_KEYSTORE_BASE64": base64.b64encode(b"fixture key").decode(),
                  "ANDROID_KEYSTORE_PASSWORD": "fixture-password", "ANDROID_KEY_ALIAS": "fixture",
                  "ANDROID_KEY_PASSWORD": "fixture-key-password"}
        result = signing_environment(values, self.root)
        key = Path(result["ANDROID_KEYSTORE_FILE"])
        self.assertEqual("private", result["SERTUM_SIGNING_MODE"])
        self.assertEqual(b"fixture key", key.read_bytes())
        self.assertEqual(0o600, key.stat().st_mode & 0o777)
        env = self.root / "environment"
        append_environment(env, {"VALUE": "one\nTWO=not-an-injected-variable"})
        text = env.read_text().splitlines()
        delimiter = text[0].split("<<")[1]
        self.assertEqual(delimiter, text[-1])

    def test_packaged_apk_checksum_and_version_trace_the_exact_build(self):
        directory, info = self.packaged()
        self.assertEqual(42, info["version_code"])
        self.assertEqual(COMMIT, info["commit"])
        self.assertEqual(hashlib.sha256((directory / info["apk"]).read_bytes()).hexdigest(), info["sha256"])
        self.assertEqual(f"{info['sha256']}  {info['apk']}\n", (directory / "SHA256SUMS").read_text())

    def test_package_rejects_split_apks(self):
        source, metadata = self.fixture()
        metadata["elements"][0]["filters"] = [{"filterType": "ABI", "value": "arm64-v8a"}]
        (source / "output-metadata.json").write_text(json.dumps(metadata))
        with self.assertRaises(ValueError):
            package_release(source, self.root / "out", COMMIT, "42", "fallback")

    def test_package_rejects_output_paths_outside_the_apk_directory(self):
        source, metadata = self.fixture()
        metadata["elements"][0]["outputFile"] = "../../private.keystore"
        (source / "output-metadata.json").write_text(json.dumps(metadata))
        with self.assertRaises(ValueError):
            package_release(source, self.root / "out", COMMIT, "42", "fallback")

    def test_package_rejects_an_apk_with_the_wrong_version_code(self):
        source, _ = self.fixture()
        with self.assertRaises(ValueError):
            package_release(source, self.root / "out", COMMIT, "43", "fallback")

    def test_new_release_uploads_assets_before_publishing_and_marks_current_main_latest(self):
        directory, _ = self.packaged()
        client = FakeGh()
        publish(directory, "owner/repo", COMMIT, "refs/heads/main", client)
        operations = [call[:2] for call in client.calls]
        self.assertLess(operations.index(("release", "create")), operations.index(("release", "upload")))
        edits = [call for call in client.calls if call[:2] == ("release", "edit")]
        self.assertIn("--draft=false", edits[0])
        self.assertIn("--latest", edits[-1])

    def test_retry_updates_the_same_release_without_creating_a_duplicate(self):
        directory, _ = self.packaged()
        client = FakeGh(existing=True)
        publish(directory, "owner/repo", COMMIT, "refs/heads/main", client)
        self.assertFalse(any(call[:2] == ("release", "create") for call in client.calls))
        self.assertTrue(any("--clobber" in call for call in client.calls))

    def test_an_older_commit_does_not_replace_the_latest_release(self):
        directory, _ = self.packaged()
        client = FakeGh(current="b" * 40)
        publish(directory, "owner/repo", COMMIT, "refs/heads/main", client)
        self.assertFalse(any("--latest" in call for call in client.calls))

    def test_no_release_is_created_for_bad_checksums_or_a_feature_branch(self):
        directory, info = self.packaged()
        client = FakeGh()
        with self.assertRaises(ValueError):
            publish(directory, "owner/repo", COMMIT, "refs/heads/feature", client)
        (directory / info["apk"]).write_bytes(b"changed after checksum")
        with self.assertRaises(ValueError):
            publish(directory, "owner/repo", COMMIT, "refs/heads/main", client)
        self.assertEqual([], client.calls)

    def test_an_existing_release_with_a_different_commit_is_not_overwritten(self):
        directory, _ = self.packaged()
        client = FakeGh(existing=True, target="c" * 40)
        with self.assertRaises(ValueError):
            publish(directory, "owner/repo", COMMIT, "refs/heads/main", client)
        self.assertEqual(1, len(client.calls))


class FakeGh:
    def __init__(self, existing=False, current=COMMIT, target=COMMIT):
        self.existing, self.current, self.target = existing, current, target
        self.calls = []

    def __call__(self, *args, check=True):
        self.calls.append(args)
        if args[:2] == ("release", "view"):
            if not self.existing:
                return subprocess.CompletedProcess(args, 1, "", "release not found")
            return subprocess.CompletedProcess(args, 0, json.dumps({"targetCommitish": self.target, "isDraft": False}), "")
        return subprocess.CompletedProcess(args, 0, self.current if args[0] == "api" else "", "")
