plugins {
    id("com.danilovfa.android.feature")
}

android {
    namespace = "com.danilovfa.feature.root"
}

dependencies {
    implementation(project(":features:record"))
    implementation(project(":features:analyze"))
}