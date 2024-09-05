plugins {
    id("com.danilovfa.android.library")
    id("com.danilovfa.android.library.compose")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.danilovfa.core"
}

dependencies {
    implementation(project(":common:resources"))

    api(libs.kotlin.datetime)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.bundles.decompose)
    implementation(libs.bundles.mvikotlin)
    implementation(libs.timber)
    implementation(libs.androidx.datastore)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.google.crypto)
}