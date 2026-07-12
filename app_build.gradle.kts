plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.aistudio.dieselstationsms.kxmpzq"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aistudio.dieselstationsms.kxmpzq"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "6.0-native"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // ═══════════════════════════════════════════════════════════
    // Android Core
    // ═══════════════════════════════════════════════════════════
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ═══════════════════════════════════════════════════════════
    // Jetpack Compose
    // ═══════════════════════════════════════════════════════════
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // ═══════════════════════════════════════════════════════════
    // Navigation Compose
    // ═══════════════════════════════════════════════════════════
    implementation(libs.androidx.navigation.compose)

    // ═══════════════════════════════════════════════════════════
    // ViewModel + Lifecycle
    // ═══════════════════════════════════════════════════════════
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ═══════════════════════════════════════════════════════════
    // Coroutines
    // ═══════════════════════════════════════════════════════════
    implementation(libs.kotlinx.coroutines.android)

    // ═══════════════════════════════════════════════════════════
    // WorkManager (Backup)
    // ═══════════════════════════════════════════════════════════
    implementation(libs.androidx.work.runtime.ktx)

    // ═══════════════════════════════════════════════════════════
    // Security (EncryptedSharedPreferences)
    // ═══════════════════════════════════════════════════════════
    implementation(libs.androidx.security.crypto)

    // ═══════════════════════════════════════════════════════════
    // Biometric
    // ═══════════════════════════════════════════════════════════
    implementation(libs.androidx.biometric)

    // ═══════════════════════════════════════════════════════════
    // Networking (Gemini API only — no localhost)
    // ═══════════════════════════════════════════════════════════
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // ═══════════════════════════════════════════════════════════
    // REMOVED: NanoHTTPD, WebView dependencies
    // ═══════════════════════════════════════════════════════════
    // implementation("org.nanohttpd:nanohttpd:2.3.1") ❌ REMOVED
    // implementation("androidx.webkit:webkit:1.8.0") ❌ REMOVED

    // ═══════════════════════════════════════════════════════════
    // Testing
    // ═══════════════════════════════════════════════════════════
    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.espresso.core)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}