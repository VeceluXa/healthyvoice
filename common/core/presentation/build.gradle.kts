plugins {
    alias(libs.plugins.danilovfa.android.library.common)
    alias(libs.plugins.danilovfa.android.library.compose)
}

android {
    namespace = "com.danilovfa.core.presentation"
}

dependencies {
    implementation(project(":common:resources"))

    implementation(libs.kermit)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.paging.compose)
    implementation(libs.kotlin.datetime)
}