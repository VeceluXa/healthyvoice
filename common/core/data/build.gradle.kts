plugins {
    alias(libs.plugins.danilovfa.android.library.common)
}

android {
    namespace = "com.danilovfa.core.data"
}

dependencies {
    implementation(libs.kotlin.serialization.json)
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.datastore)
    implementation(libs.google.crypto)
}