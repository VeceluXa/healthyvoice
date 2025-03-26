plugins {
    id("com.danilovfa.android.library.data")
}

android {
    namespace = "com.danilovfa.data.record"
}

dependencies {
    implementation(project(":data:common"))
    implementation(project(":feature:record:domain"))
}