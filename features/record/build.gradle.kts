plugins {
    id("com.danilovfa.android.feature")
}

android {
    namespace = "com.danilovfa.feature.record"
}

dependencies {
    implementation(project(":data:common"))
    implementation(project(":common:libs:recorder"))
    implementation(project(":data:record"))

    implementation(libs.androidx.activity.compose)
}