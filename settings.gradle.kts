// Google's mirror of Maven Central is tried first; Maven Central stays as the fallback.
pluginManagement {
    repositories {
        google()
        maven("https://maven-central.storage-download.googleapis.com/maven2/") {
            name = "GoogleMavenCentralMirror"
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven("https://maven-central.storage-download.googleapis.com/maven2/") {
            name = "GoogleMavenCentralMirror"
        }
        mavenCentral()
    }
}

rootProject.name = "Sertum-Rosarum"
include(":app")
