plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.chaquo)
}

android {
    namespace = "com.danilovfa.healthyvoice"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.danilovfa.healthyvoice"
        minSdk = 26
        targetSdk = 34
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
        }
    }
}

dependencies {
    implementation(project(":features:root"))
    implementation(project(":common:uikit"))
    implementation(project(":common:core"))
    implementation(project(":common:libs:recorder"))
    implementation(project(":data:record"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))

    implementation(libs.bundles.decompose)
    implementation(libs.bundles.mvikotlin)
    implementation(libs.bundles.koin)
    implementation(libs.timber)
}