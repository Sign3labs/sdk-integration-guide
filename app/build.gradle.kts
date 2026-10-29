import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
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

android {
    namespace = "com.android.sdk_integration_guide"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.android.sdk_integration_guide"
        minSdk = 23 // Sign3 SDK requires API 23+
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "SIGN3_CLIENT_ID", "\"${sign3("SIGN3_CLIENT_ID")}\"")
        buildConfigField("String", "SIGN3_CLIENT_SECRET", "\"${sign3("SIGN3_CLIENT_SECRET")}\"")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.sign3.intelligence)
}
