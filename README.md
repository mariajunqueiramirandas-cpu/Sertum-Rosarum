# Sertum-Rosarum

An app for helping with the Holy Rosary.

## Tech stack

- Kotlin 2.1 + Jetpack Compose (Compose Compiler Gradle plugin)
- **No Material libraries** — UI is built on `androidx.compose.ui` + `foundation` only, with a custom design system under `ui/theme` (colors/typography provided via `CompositionLocal`)
- AGP 8.9.1, Gradle 8.11.1, Kotlin DSL + version catalog (`gradle/libs.versions.toml`)
- compileSdk 35, targetSdk 35, **minSdk 33** (Android 13+ only)
- JDK 17 toolchain, edge-to-edge enabled

## Project structure

```
app/src/main/java/com/sertumrosarum/
├── SertumApplication.kt
├── MainActivity.kt
└── ui/
    ├── theme/        # custom design system: SertumTheme, colors, typography
    ├── navigation/   # NavHost + route constants
    └── home/         # placeholder screen (feature folders go here)
```

## Requirements

- JDK 17
- Latest stable Android Studio

## Getting started

```bash
gradle wrapper        # one-time: generates gradlew and the wrapper jar
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

## CI

GitHub Actions (`.github/workflows/android-ci.yml`) runs unit tests and lint on every push and pull request.
