plugins {
    id("com.danilovfa.android.library.data")
}

android {
    namespace = "com.danilovfa.data.record"
}

dependencies {
    implementation(project(":feature:record:domain"))
    implementation(project(":feature:analysis:domain"))
    implementation(project(":feature:common:domain"))
    implementation(project(":common:libs:recorder"))
    implementation(project(":feature:common:data"))
}