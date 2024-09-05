
import com.danilovfa.healthyvoice.libs
import org.gradle.api.Plugin import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.danilovfa.android.library")
                apply("com.danilovfa.android.library.compose")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                add("implementation", project(":common:core"))
                add("implementation", project(":common:uikit"))
                add("implementation", project(":common:resources"))

                add("implementation", libs.findBundle("decompose").get())
                add("implementation", libs.findBundle("mvikotlin").get())
                add("implementation", libs.findBundle("koin").get())
                add("implementation", libs.findLibrary("timber").get())
                add("implementation", libs.findLibrary("kotlin.serialization.json").get())
            }
        }
    }
}