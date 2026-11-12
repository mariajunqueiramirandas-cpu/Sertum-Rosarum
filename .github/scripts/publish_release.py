#!/usr/bin/env python3
"""Publish a tested APK idempotently, without moving latest to an older commit."""

import argparse
import hashlib
import json
import os
from pathlib import Path
import subprocess


def gh(*args, check=True):
    return subprocess.run(["gh", *args], text=True, capture_output=True, check=check)


def publish(directory, repository, commit, ref, run=gh):
    if ref != "refs/heads/main":
        raise ValueError("Releases may only be published from main")
    directory = Path(directory).resolve()
    info = json.loads((directory / "build-info.json").read_text())
    if info["commit"] != commit:
        raise ValueError("The release artifact belongs to a different commit")
    apk = (directory / info["apk"]).resolve()
    if not apk.is_relative_to(directory) or not apk.is_file():
        raise ValueError("Invalid APK asset path")
    with apk.open("rb") as content:
        digest = hashlib.file_digest(content, "sha256").hexdigest()
    if digest != info["sha256"] or (directory / "SHA256SUMS").read_text() != f"{digest}  {apk.name}\n":
        raise ValueError("Release APK checksum verification failed")
    if info["tag"] != f"build-{info['build_number']}-{commit[:12]}":
        raise ValueError("The release tag does not match its build metadata")
    tag = info["tag"]
    title = f"Sertum Rosarum {info['version_name']}"
    signing = "Stable public development fallback key" if info["signing"] == "fallback" else "Private repository signing key"
    notes = directory / "release-notes.md"
    notes.write_text(
        f"## Automatic Android APK\n\n"
        f"- Commit: [{commit[:12]}](https://github.com/{repository}/commit/{commit})\n"
        f"- Version: **{info['version_name']}** · Android version code **{info['version_code']}**\n"
        f"- Requires **Android 13 or newer**.\n"
        f"- Signing: {signing}.\n"
        f"- Optimized release build: R8 code shrinking and resource shrinking enabled.\n\n"
        f"Download the APK below. `SHA256SUMS` and `build-info.json` identify the exact build. "
        f"This release was published after the build, unit tests, lint, device UI tests, and release launch check passed.\n"
    )
    existing = run("release", "view", tag, "--repo", repository, "--json", "targetCommitish,isDraft", check=False)
    if existing.returncode:
        if "not found" not in existing.stderr.lower() and "404" not in existing.stderr:
            raise RuntimeError("Could not check the existing release: " + existing.stderr)
        run("release", "create", tag, "--repo", repository, "--target", commit, "--title", title,
            "--notes-file", str(notes), "--draft")
        draft = True
    else:
        release = json.loads(existing.stdout)
        if release["targetCommitish"] != commit:
            raise ValueError("An existing release points to another commit")
        draft = release["isDraft"]
    run("release", "upload", tag, str(apk), str(directory / "SHA256SUMS"), str(directory / "build-info.json"),
        "--repo", repository, "--clobber")
    edit = ["release", "edit", tag, "--repo", repository, "--title", title, "--notes-file", str(notes)]
    if draft:
        edit.extend(["--draft=false", "--latest=false"])
    run(*edit)
    current = run("api", f"repos/{repository}/git/ref/heads/main", "--jq", ".object.sha").stdout.strip()
    if current == commit:
        run("release", "edit", tag, "--repo", repository, "--latest")
    url = f"https://github.com/{repository}/releases/tag/{tag}"
    return url


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("directory")
    args = parser.parse_args()
    url = publish(args.directory, os.environ["GITHUB_REPOSITORY"], os.environ["GITHUB_SHA"], os.environ["GITHUB_REF"])
    print(url)
    if os.environ.get("GITHUB_STEP_SUMMARY"):
        with Path(os.environ["GITHUB_STEP_SUMMARY"]).open("a") as summary:
            summary.write(f"### APK published\n\n[Download this release]({url})\n")
