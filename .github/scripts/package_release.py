#!/usr/bin/env python3
"""Stage one universal APK with verifiable checksums and commit metadata."""

import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import zipfile


def package_release(source, output, commit, build_number, signing_mode):
    if not re.fullmatch(r"[a-f0-9]{40}", commit):
        raise ValueError("A full commit SHA is required")
    if not build_number.isdigit() or int(build_number) < 1:
        raise ValueError("A positive build number is required")
    if signing_mode not in ("fallback", "private"):
        raise ValueError("Unknown signing mode")
    source = Path(source).resolve()
    metadata = json.loads((source / "output-metadata.json").read_text())
    elements = metadata["elements"]
    if len(elements) != 1 or elements[0].get("filters"):
        raise ValueError("Publish one universal APK, not a split APK")
    element = elements[0]
    apk = (source / element["outputFile"]).resolve()
    if not apk.is_relative_to(source) or apk.suffix != ".apk" or not apk.is_file():
        raise ValueError("APK must be a file inside the release output directory")
    version = element["versionName"]
    if not re.fullmatch(r"[0-9A-Za-z][0-9A-Za-z.+_-]{0,99}", version):
        raise ValueError("Invalid release version name")
    if type(element["versionCode"]) is not int or element["versionCode"] != int(build_number):
        raise ValueError("APK version code must match the workflow build number")
    with zipfile.ZipFile(apk) as archive:
        if not {"AndroidManifest.xml", "classes.dex"}.issubset(archive.namelist()):
            raise ValueError("The release is not an Android application APK")
    directory = Path(output) / f"build-{build_number}-{commit[:12]}"
    directory.mkdir(parents=True, exist_ok=True)
    filename = f"SertumRosarum-{version}-{commit[:12]}.apk"
    target = directory / filename
    shutil.copy2(apk, target)
    with target.open("rb") as content:
        digest = hashlib.file_digest(content, "sha256").hexdigest()
    info = {"commit": commit, "build_number": int(build_number), "version_name": version,
            "version_code": element["versionCode"], "signing": signing_mode,
            "apk": filename, "sha256": digest, "size_bytes": target.stat().st_size,
            "tag": f"build-{build_number}-{commit[:12]}"}
    (directory / "SHA256SUMS").write_text(f"{digest}  {filename}\n")
    (directory / "build-info.json").write_text(json.dumps(info, indent=2) + "\n")
    return directory, info


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source", default="app/build/outputs/apk/release")
    parser.add_argument("--output", default="app/build/outputs/distribution")
    args = parser.parse_args()
    directory, info = package_release(args.source, args.output, os.environ["GITHUB_SHA"],
                                      os.environ["GITHUB_RUN_NUMBER"], os.environ["SERTUM_SIGNING_MODE"])
    with Path(os.environ["GITHUB_OUTPUT"]).open("a") as output:
        output.write(f"directory={directory}\n")
    print(json.dumps(info, indent=2))


if __name__ == "__main__":
    main()
