plugins {
    id("com.danilovfa.android.library")
    id("com.danilovfa.android.library.compose")
}

android {
    namespace = "com.danilovfa.common.base"
}

dependencies {
    implementation(project(":common:core:presentation"))
    implementation(project(":common:core:domain"))

    implementation(libs.bundles.decompose)
}