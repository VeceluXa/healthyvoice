package com.danilovfa.healthyvoice

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.versions.android.compose.compiler.get()
        }

        dependencies {
            val bom = libs.compose.bom
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
        }
    }


}