plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.chaquo)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.danilovfa.healthyvoice"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.danilovfa.healthyvoice"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

chaquopy {
    defaultConfig {
        pip {
            install("numpy")
            install("matplotlib")
            install("soundfile")
            install("scipy")
            install("numba")
        }
    }
}

dependencies {
    implementation(project(":feature:root:presentation"))
    implementation(project(":common:uikit"))
    implementation(project(":common:libs:recorder"))
    implementation(project(":feature:record:data"))

    implementation(project(":common:base"))
    implementation(project(":common:core:presentation"))
    implementation(project(":common:core:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))

    implementation(libs.bundles.decompose)
    implementation(libs.bundles.mvikotlin)
    implementation(libs.bundles.koin)
    implementation(libs.timber)
}