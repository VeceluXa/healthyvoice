plugins {
    id("com.danilovfa.android.library.data")
}

android {
    namespace = "com.danilovfa.data.analysis"
}

dependencies {
    implementation(project(":feature:common:data"))
    implementation(project(":feature:common:domain"))
    implementation(project(":feature:analysis:domain"))
}