plugins {
    alias(libs.plugins.danilovfa.kotlin.library.domain)
}

dependencies {
    implementation(project(":feature:common:domain"))
}