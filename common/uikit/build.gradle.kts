plugins {
    id("com.danilovfa.android.library")
    id("com.danilovfa.android.library.compose")
}

android {
    namespace = "com.danilovfa.common.uikit"
}

dependencies {
    implementation(project(":common:base"))
    implementation(project(":common:core:presentation"))
    implementation(project(":common:core:domain"))
    implementation(project(":common:resources"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle)
    implementation(libs.kotlin.datetime)

    implementation(libs.bundles.compose)
}