plugins {
    id("com.danilovfa.kotlin.library.domain")
}

dependencies {
    implementation(project(":feature:common:domain"))
}