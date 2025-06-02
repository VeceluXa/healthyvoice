plugins {
    id("com.danilovfa.android.library.presentation")
    id("com.xcporter.metaview")
}

android {
    namespace = "com.danilovfa.presentation.patient"
}

dependencies {
    implementation(project(":feature:common:domain"))
    implementation(project(":feature:patient:domain"))
    implementation(project(":feature:analysis:domain"))
}

generateUml {
    classTree {  }
    functionTree {  }
}