plugins {
    id("com.danilovfa.android.library.presentation")
}

android {
    namespace = "com.danilovfa.feature.cut"
}

dependencies {
    implementation(project(":feature:common:domain"))
    implementation(project(":feature:record:domain"))
}