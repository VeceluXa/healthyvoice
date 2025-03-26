plugins {
    id("com.danilovfa.android.library")
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