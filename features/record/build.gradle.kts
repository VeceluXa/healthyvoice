plugins {
    id("com.danilovfa.android.feature")
}

android {
    namespace = "com.danilovfa.feature.record"
}

dependencies {
    implementation(project(":common:libs:recorder"))

    implementation(libs.androidx.activity.compose)
}