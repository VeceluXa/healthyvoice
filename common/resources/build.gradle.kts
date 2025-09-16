plugins {
    alias(libs.plugins.danilovfa.android.library.common)
    alias(libs.plugins.danilovfa.android.library.compose)
}

android {
    namespace = "com.danilovfa.resources"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.compose.material.icons)
}