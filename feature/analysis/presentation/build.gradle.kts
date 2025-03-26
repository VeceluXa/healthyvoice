plugins {
    id("com.danilovfa.android.library.presentation")
    alias(libs.plugins.chaquo)
}

android {
    namespace = "com.danilovfa.presentation.analysis"

    defaultConfig {
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }
}

dependencies {
    implementation(project(":common:libs:recorder"))
    implementation(project(":feature:record:domain"))
}