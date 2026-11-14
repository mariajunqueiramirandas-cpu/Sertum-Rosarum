plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.roborazzi)
}

val releaseStore = providers.environmentVariable("ANDROID_KEYSTORE_FILE").orNull
val releaseStorePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").orNull
val releaseAlias = providers.environmentVariable("ANDROID_KEY_ALIAS").orNull
val releaseKeyPassword = providers.environmentVariable("ANDROID_KEY_PASSWORD").orNull
val signingValues = listOf(releaseStore, releaseStorePassword, releaseAlias, releaseKeyPassword)
require(signingValues.all { it.isNullOrBlank() } || signingValues.all { !it.isNullOrBlank() }) {
    "Set all four ANDROID_KEYSTORE_FILE / ANDROID_KEYSTORE_PASSWORD / ANDROID_KEY_ALIAS / ANDROID_KEY_PASSWORD values, or leave all unset to use the public fallback signer."
}
val buildVersionCode = providers.environmentVariable("SERTUM_VERSION_CODE").map {
    it.toInt().also { code -> require(code in 1..2_100_000_000) { "Invalid Android version code" } }
}.orElse(1)
val buildVersionName = providers.environmentVariable("SERTUM_VERSION_NAME")
    .orElse(providers.gradleProperty("sertum.versionName"))

// Must match the SDK 35 artifact of the Robolectric version in libs.versions.toml.
val robolectricAndroidAll by configurations.creating { isCanBeConsumed = false }
val robolectricJarsDir = layout.buildDirectory.dir("robolectric-jars")
val stageRobolectricAndroidAll by tasks.registering(Sync::class) {
    from(robolectricAndroidAll)
    into(robolectricJarsDir)
}

android {
    namespace = "com.sertumrosarum"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sertumrosarum"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = providers.gradleProperty("sertum.versionName").get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = rootProject.file(".github/signing/fallback.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            if (!releaseStore.isNullOrBlank()) {
                storeFile = file(releaseStore)
                storePassword = releaseStorePassword
                keyAlias = releaseAlias
                keyPassword = releaseKeyPassword
            } else {
                initWith(getByName("debug"))
            }
            enableV1Signing = false
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = false
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = false
        resValues = false
    }

    testOptions.unitTests.isIncludeAndroidResources = true
    testOptions.unitTests.all {
        it.maxHeapSize = providers.gradleProperty("sertum.testHeap").orElse("1g").get()
        // Robolectric runs offline against the jar staged below, so it never hits Maven Central directly.
        it.dependsOn(stageRobolectricAndroidAll)
        it.systemProperty("robolectric.offline", "true")
        it.systemProperty("robolectric.dependency.dir", robolectricJarsDir.get().asFile.absolutePath)
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Wire version providers lazily so a new CI build number can reuse configuration.
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            output.versionCode.set(buildVersionCode)
            output.versionName.set(buildVersionName)
        }
    }
}

kotlin {
    jvmToolchain(17)
}

// Tooling can bring Material transitively; the sanctuary uses only our Foundation controls.
configurations.configureEach {
    exclude(group = "androidx.compose.material")
    exclude(group = "androidx.compose.material3")
}

dependencies {
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.datastore.preferences)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.foundation)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    robolectricAndroidAll("org.robolectric:android-all-instrumented:15-robolectric-12650502-i7")
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.androidx.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}

ksp { arg("room.schemaLocation", "$projectDir/schemas") }
