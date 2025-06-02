plugins {
    id("com.danilovfa.android.library.presentation")
}

android {
    namespace = "com.danilovfa.presentation.export"
}

dependencies {
    implementation(project(":feature:export:domain"))
    implementation(libs.koin.workmanager)
}