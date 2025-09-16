import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.danilovfa.healthyvoice.build-logic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)

    // Workaround for version catalog working inside precompiled scripts
    // Issue - https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "com.danilovfa.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "com.danilovfa.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibraryData") {
            id = "com.danilovfa.android.library.data"
            implementationClass = "AndroidLibraryDataConventionPlugin"
        }
        register("androidFeature") {
            id = "com.danilovfa.android.library.presentation"
            implementationClass = "AndroidLibraryPresentationConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "com.danilovfa.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("kotlinLibraryDomain") {
            id = "com.danilovfa.kotlin.library.domain"
            implementationClass = "KotlinLibraryDomainConventionPlugin"
        }
    }
}