plugins {
    alias(libs.plugins.danilovfa.android.library.common)
    alias(libs.plugins.danilovfa.android.library.compose)
}

android {
    namespace = "com.danilovfa.common.base"
}

dependencies {
    implementation(project(":common:core:presentation"))
    implementation(project(":common:core:domain"))

    implementation(libs.bundles.decompose)
}