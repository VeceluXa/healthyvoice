import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.chaquo)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.danilovfa.healthyvoice"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.danilovfa.healthyvoice"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
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

    implementation(project(":common:base"))
    implementation(project(":common:core:presentation"))
    implementation(project(":common:core:domain"))

    implementation(project(":feature:common:data"))
    implementation(project(":feature:patient:data"))
    implementation(project(":feature:record:data"))
    implementation(project(":feature:analysis:data"))
    implementation(project(":feature:export:presentation"))
    implementation(project(":feature:export:data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))

    implementation(libs.bundles.decompose)
    implementation(libs.bundles.mvikotlin)
    implementation(libs.bundles.koin)
    implementation(libs.koin.workmanager)
    implementation(libs.timber)
}