plugins {
    id("com.danilovfa.android.feature")
    alias(libs.plugins.chaquo)
}

android {
    namespace = "com.danilovfa.feature.analyze"

    defaultConfig {
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }
}

dependencies {
    implementation(project(":common:libs:recorder"))
    implementation(project(":data:common"))
    implementation(project(":data:record"))
}