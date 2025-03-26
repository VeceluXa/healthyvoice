plugins {
    id("com.danilovfa.android.library.presentation")
}

android {
    namespace = "com.danilovfa.presentation.root"
}

dependencies {
    implementation(project(":feature:record:domain"))
    implementation(project(":feature:record:presentation"))
    implementation(project(":feature:analysis:presentation"))
    implementation(project(":feature:patient:presentation"))
    implementation(project(":feature:cut:presentation"))
}