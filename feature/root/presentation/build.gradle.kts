plugins {
    id("com.danilovfa.android.feature")
}

android {
    namespace = "com.danilovfa.presentation.root"
}

dependencies {
    implementation(project(":feature:record:domain"))
    implementation(project(":feature:record:presentation"))
    implementation(project(":feature:analyze:presentation"))
    implementation(project(":feature:cut:presentation"))
}