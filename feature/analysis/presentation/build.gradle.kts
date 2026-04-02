plugins {
    alias(libs.plugins.danilovfa.android.library.presentation)
    alias(libs.plugins.chaquo)
}

android {
    namespace = "com.danilovfa.presentation.analysis"

    defaultConfig {
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    implementation(project(":common:libs:recorder"))
    implementation(project(":feature:record:domain"))
    implementation(project(":feature:common:domain"))
    implementation(project(":feature:analysis:domain"))
    implementation(project(":feature:export:presentation"))
}
