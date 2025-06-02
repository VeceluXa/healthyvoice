plugins {
    id("com.danilovfa.android.library.presentation")
    id("com.xcporter.metaview")
}

android {
    namespace = "com.danilovfa.libs.recorder"
}

dependencies {
}

generateUml {
    classTree {  }
    functionTree {  }
}