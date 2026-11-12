# Fast builds and automatic APK releases

The [Android workflow](../.github/workflows/android-ci.yml) builds, checks, and distributes the app in one pipeline. It runs on pull requests, every push to `main`, and manual dispatch. Only `main` can publish a GitHub Release.

## What happens on main

1. Restore Gradle dependency, transform, and task-output caches.
2. Set a memory budget appropriate to the runner, assign an increasing Android version code, and select private signing or the stable fallback.
3. Build the debug app, instrumented test APK, and optimized release app in **one Gradle invocation**, alongside unit tests and lint.
4. Verify the release signature and ZIP alignment; stage the APK with a SHA-256 checksum and source metadata.
5. Boot a cached Android 13 emulator. Install the prebuilt APKs, run the five UI flows, and collect screenshots before uninstalling the test target. Install the real minified release and verify that its sanctuary opens.
6. After the build and device job pass, publish the already-built APK on GitHub Releases. The publishing job does not compile the app again.

Every successful main run produces:

- `SertumRosarum-<version>-<short-commit>.apk` — an installable, non-debuggable, minified release APK.
- `SHA256SUMS` — verifies the APK bytes.
- `build-info.json` — full source commit, Android version code, version name, build number, signing mode, file size, and checksum.

The release tag is `build-<workflow-run-number>-<short-commit>`. Rerunning the same workflow updates the same release rather than creating a duplicate. A draft is made visible only after its assets are uploaded. Older builds can finish and get their release without moving the **Latest** pointer away from the current main commit.

## Build versions

The base version is defined once as `sertum.versionName` in [`gradle.properties`](../gradle.properties). CI appends `-build.<run-number>` and uses the workflow run number as the Android `versionCode`.

For example, run 42 for commit `abcdef123456…` produces version `0.1.0-build.42`, Android version code `42`, and tag `build-42-abcdef123456`.

Local builds retain version `0.1.0` and code `1` unless `SERTUM_VERSION_NAME` and `SERTUM_VERSION_CODE` are provided. Keep the workflow's run-number sequence when maintaining the release pipeline so Android update versions keep increasing.

## Signing with no setup

A fixed [public development fallback signer](../.github/signing/README.md) is included, as requested. Both debug builds and fallback release builds use it, so builds from different runners share an update identity. The private key is intentionally public and is suitable for development distribution.

The **release variant** still enables R8 code shrinking and resource shrinking and disables debuggability. Using the fallback does not turn the release into a debug APK.

For private signing, add all four optional repository Actions secrets:

| Secret | Contents |
| --- | --- |
| `ANDROID_KEYSTORE_BASE64` | The private keystore encoded as base64 |
| `ANDROID_KEYSTORE_PASSWORD` | Store password |
| `ANDROID_KEY_ALIAS` | Key alias |
| `ANDROID_KEY_PASSWORD` | Key password |

Private credentials are used only on `main`. The keystore is decoded into the runner's temporary directory, is not uploaded, and is removed after the build. Incomplete signing credentials fail explicitly. Pull requests always use the public fallback.

An APK installed with a different signing certificate, such as an older local debug build, must be uninstalled before changing signing identities. Future fallback builds keep the same certificate.

## Memory and CPU budgets

| Environment | Gradle heap | Kotlin daemon | Worker cap |
| --- | --- | --- | --- |
| Project defaults | 4 GiB | 1 GiB | 4 |
| Standard 16 GiB CI runner | 6 GiB | 2 GiB | 4 |
| Smaller CI runner | Scaled down automatically | Scaled down automatically | Available CPUs, up to 4 |
| Emulator job | No Gradle or Kotlin compiler | None | Emulator: 2 virtual CPUs, 2 GiB RAM |

The [configuration helper](../.github/scripts/configure_build.py) accounts for physical and cgroup memory limits and leaves room for the OS, test JVM, and metaspace. It uses Parallel GC and limits the JVM's processor count to the worker budget.

The project enables the Gradle daemon, build cache, parallel execution, parallel configuration-cache loading, filesystem watching, and Kotlin incremental compilation with classpath snapshots. Jetifier remains disabled. Unused BuildConfig and generated resource-value generation are disabled.

CI version values are wired through lazy Android variant-output providers. Changing the build number can reuse the configuration cache instead of forcing project configuration again.

## Where the speed comes from

- **Compile once:** the emulator downloads APKs from the build job; it never repeats compilation or Gradle dependency resolution.
- **One build invocation:** debug, test, release, unit tests, and lint share configuration and dependency work.
- **Warm task outputs:** trusted same-repository PRs can write their own branch-scoped Gradle caches. Fork PRs remain read-only for this cache.
- **Warm Android infrastructure:** the system image and AVD snapshot are cached. The AVD key includes the emulator revision, image revision, and fallback signing identity.
- **No repeated APK compression:** APK and PNG artifacts use upload compression level `0` because those formats are already compressed.
- **Useful cancellation:** obsolete PR builds are canceled. Main runs use distinct concurrency groups and are allowed to finish and publish.
- **Separate resource-heavy work:** emulator execution happens after compilation on another runner, so the compiler and emulator do not compete for memory or CPU.
- **Small distribution:** R8 removes unused code and resources from the release APK; preview tooling is a debug-only dependency.

For encrypted configuration-cache reuse across main runs, optionally set `GRADLE_ENCRYPTION_KEY` to the output of `openssl rand -base64 16`. Without it, dependency and task caches still work; configuration-cache data is not uploaded by the Gradle action.

No `clean` task is run during normal CI. Use it only when diagnosing a specific stale-output problem.

## Local verification

With JDK 17 and Android SDK 35 configured:

```bash
python3 -m unittest discover -s .github/scripts/tests -v
./gradlew assembleDebug assembleDebugAndroidTest assembleRelease testDebugUnitTest lintDebug
```

The release APK is at `app/build/outputs/apk/release/app-release.apk`. It is already signed with the stable fallback unless private signing environment variables are provided.

To exercise exactly the device job locally, put the three APKs in `app/build/ci-apks`, start an Android 13+ emulator, and run:

```bash
bash .github/scripts/run-device-tests.sh app/build/ci-apks
```

This helper is intended for a disposable emulator: it removes the previous test/app installations so signing identities and saved app data cannot contaminate a run. It saves device logs, six screenshots, and the optimized release launch proof under `app/build`.
