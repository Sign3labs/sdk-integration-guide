import java.util.Properties

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Sign3 credentials are read from credentials.properties in the project root (git-ignored),
// falling back to environment variables with the same names. Expected keys:
//   SIGN3_ARTIFACTORY_USERNAME, SIGN3_ARTIFACTORY_PASSWORD, SIGN3_CLIENT_ID, SIGN3_CLIENT_SECRET
val sign3Props = Properties().apply {
    val f = rootDir.resolve("credentials.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
fun sign3(key: String): String =
    sign3Props.getProperty(key) ?: System.getenv(key) ?: ""

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://sign3.jfrog.io/artifactory/intelligence-generic-local/")
            credentials {
                username = sign3("SIGN3_ARTIFACTORY_USERNAME")
                password = sign3("SIGN3_ARTIFACTORY_PASSWORD")
            }
        }
    }
}

rootProject.name = "sdk-integration-guide"
include(":app")
