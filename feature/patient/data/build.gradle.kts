plugins {
    id("com.danilovfa.android.library.data")
}

android {
    namespace = "com.danilovfa.data.patient"
}

dependencies {
    implementation(project(":feature:common:data"))
    implementation(project(":feature:common:domain"))
    implementation(project(":feature:patient:domain"))
}