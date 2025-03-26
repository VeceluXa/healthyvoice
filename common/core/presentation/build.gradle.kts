plugins {
    id("com.danilovfa.android.library")
    id("com.danilovfa.android.library.compose")
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