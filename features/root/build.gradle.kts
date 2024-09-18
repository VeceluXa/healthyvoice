plugins {
    id("com.danilovfa.android.feature")
}

android {
    namespace = "com.danilovfa.feature.root"
}

dependencies {
    implementation(project(":data:common"))
    implementation(project(":features:record"))
    implementation(project(":features:analyze"))
    implementation(project(":features:cut"))
}