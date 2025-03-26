plugins {
    id("com.danilovfa.android.library.data")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.danilovfa.data.common"
}

dependencies {
    implementation(project(":feature:common:domain"))

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
}