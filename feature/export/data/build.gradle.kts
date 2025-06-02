plugins {
    id("com.danilovfa.android.library.data")
}

android {
    namespace = "com.danilovfa.data.export"
}

dependencies {
    implementation(project(":feature:export:domain"))
    implementation(project(":feature:common:domain"))
    implementation(project(":feature:common:data"))
}