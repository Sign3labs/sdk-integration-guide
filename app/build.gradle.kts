import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Sign3 credentials live in credentials.properties (git-ignored) or environment variables.
// See credentials.properties.example for the expected keys.
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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SIGN3_CLIENT_ID", "\"${sign3("SIGN3_CLIENT_ID")}\"")
        buildConfigField("String", "SIGN3_CLIENT_SECRET", "\"${sign3("SIGN3_CLIENT_SECRET")}\"")
    }

    buildFeatures {
        buildConfig = true
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
