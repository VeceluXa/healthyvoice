
import com.danilovfa.healthyvoice.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryDataConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.danilovfa.android.library")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                add("implementation", project(":common:core:domain"))
                add("implementation", project(":common:core:data"))
                add("implementation", libs.findBundle("koin").get())
                add("implementation", libs.findLibrary("timber").get())
                add("implementation", libs.findLibrary("kotlin.serialization.json").get())
                add("implementation", libs.findLibrary("androidx.datastore").get())
            }
        }
    }
}