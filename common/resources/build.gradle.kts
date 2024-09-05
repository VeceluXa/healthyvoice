plugins {
    id("com.danilovfa.android.library")
    id("com.danilovfa.android.library.compose")
}

android {
    namespace = "com.danilovfa.resources"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.compose.material.icons)
}