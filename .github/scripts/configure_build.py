#!/usr/bin/env python3
"""Configure bounded CI memory, build versions, and optional private signing."""

import argparse
import base64
import json
import os
from pathlib import Path
import secrets
import tempfile


def memory_profile(memory_mib, processors):
    workers = max(1, min(processors, 4))
    fraction = 30 if memory_mib < 6144 else 45
    gradle = max(1024, min(6144, (memory_mib * fraction // 100) // 256 * 256))
    kotlin = 2048 if memory_mib >= 12288 else max(512, (memory_mib // 8) // 256 * 256)
    test = 1024 if memory_mib >= 8192 else 384 if memory_mib < 6144 else 512
    metaspace = 1024 if memory_mib >= 12288 else 384 if memory_mib < 6144 else 512
    kotlin_meta = 512 if memory_mib >= 12288 else 256 if memory_mib < 6144 else 384
    return {"gradle_mib": gradle, "kotlin_mib": kotlin, "test_mib": test, "workers": workers,
            "gradle_metaspace_mib": metaspace, "kotlin_metaspace_mib": kotlin_meta}


def available_memory():
    memory = int(next(line.split()[1] for line in Path("/proc/meminfo").read_text().splitlines() if line.startswith("MemTotal:"))) // 1024
    limit = Path("/sys/fs/cgroup/memory.max")
    if limit.exists() and limit.read_text().strip().isdigit():
        memory = min(memory, int(limit.read_text()) // 1024 // 1024)
    return memory


def signing_environment(environ, directory):
    names = ("ANDROID_KEYSTORE_BASE64", "ANDROID_KEYSTORE_PASSWORD", "ANDROID_KEY_ALIAS", "ANDROID_KEY_PASSWORD")
    values = [environ.get(name, "") for name in names]
    if not any(values):
        return {"SERTUM_SIGNING_MODE": "fallback"}
    if not all(values):
        raise ValueError("Private signing requires all four ANDROID_KEYSTORE_BASE64 / PASSWORD / KEY_ALIAS / KEY_PASSWORD secrets")
    content = base64.b64decode("".join(values[0].split()), validate=True)
    if not content:
        raise ValueError("The private signing keystore is empty")
    with tempfile.NamedTemporaryFile(prefix="sertum-signing-", suffix=".keystore", dir=directory, delete=False) as key:
        key.write(content)
        path = Path(key.name)
    path.chmod(0o600)
    return {"SERTUM_SIGNING_MODE": "private", "ANDROID_KEYSTORE_FILE": str(path), **dict(zip(names[1:], values[1:]))}


def append_environment(path, values):
    with Path(path).open("a") as output:
        for name, value in values.items():
            delimiter = "SERTUM_" + secrets.token_hex(16)
            output.write(f"{name}<<{delimiter}\n{value}\n{delimiter}\n")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--memory-mib", type=int)
    parser.add_argument("--processors", type=int)
    args = parser.parse_args()
    processors = args.processors or len(os.sched_getaffinity(0))
    profile = memory_profile(args.memory_mib or available_memory(), processors)
    gradle_home = Path(os.environ.get("GRADLE_USER_HOME", str(Path.home() / ".gradle")))
    gradle_home.mkdir(parents=True, exist_ok=True)
    with (gradle_home / "gradle.properties").open("a") as output:
        output.write(
            f"\norg.gradle.jvmargs=-Xmx{profile['gradle_mib']}m -XX:MaxMetaspaceSize={profile['gradle_metaspace_mib']}m -XX:+UseParallelGC -XX:ActiveProcessorCount={profile['workers']} -Dfile.encoding=UTF-8\n"
            f"kotlin.daemon.jvmargs=-Xmx{profile['kotlin_mib']}m -XX:MaxMetaspaceSize={profile['kotlin_metaspace_mib']}m -XX:+UseParallelGC -XX:ActiveProcessorCount={profile['workers']}\n"
            f"org.gradle.workers.max={profile['workers']}\nsertum.testHeap={profile['test_mib']}m\n"
        )
    properties = dict(line.split("=", 1) for line in Path("gradle.properties").read_text().splitlines() if "=" in line and not line.startswith("#"))
    number = int(os.environ["GITHUB_RUN_NUMBER"])
    if not 1 <= number <= 2_100_000_000:
        raise ValueError("The workflow run number is outside Android's version code range")
    signing = signing_environment(os.environ, os.environ["RUNNER_TEMP"])
    append_environment(os.environ["GITHUB_ENV"], {
        "SERTUM_VERSION_CODE": str(number),
        "SERTUM_VERSION_NAME": f"{properties['sertum.versionName']}-build.{number}",
        **signing,
    })
    # Only public build information is logged; private signing values are never printed.
    print(json.dumps({**profile, "version_code": number, "signing": signing["SERTUM_SIGNING_MODE"]}))


if __name__ == "__main__":
    main()
