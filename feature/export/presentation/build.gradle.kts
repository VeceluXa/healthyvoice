plugins {
    alias(libs.plugins.danilovfa.android.library.presentation)
}

android {
    namespace = "com.danilovfa.presentation.export"
}

dependencies {
    implementation(project(":feature:export:domain"))
    implementation(libs.koin.workmanager)
}