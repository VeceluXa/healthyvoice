plugins {
    id("com.danilovfa.android.library")
    id("com.danilovfa.android.library.compose")
}

android {
    namespace = "com.danilovfa.uikit"
}

dependencies {
    implementation(project(":common:resources"))
    implementation(project(":common:core"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle)
}