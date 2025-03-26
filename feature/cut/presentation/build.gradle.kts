plugins {
    id("com.danilovfa.android.feature")
}

android {
    namespace = "com.danilovfa.feature.cut"
}

dependencies {
    implementation(project(":feature:common:domain"))
    implementation(project(":feature:record:domain"))
}