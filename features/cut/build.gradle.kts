plugins {
    id("com.danilovfa.android.feature")
}

android {
    namespace = "com.danilovfa.feature.cut"
}

dependencies {
    implementation(project(":data:common"))
    implementation(project(":data:record"))
}