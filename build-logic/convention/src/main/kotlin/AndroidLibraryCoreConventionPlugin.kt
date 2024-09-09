
import com.danilovfa.healthyvoice.libs
import org.gradle.api.Plugin import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryCoreConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.danilovfa.android.library")
                apply("com.danilovfa.android.library.compose")
            }

            dependencies {
                add("implementation", project(":common:core"))

                add("implementation", libs.findBundle("koin").get())
                add("implementation", libs.findLibrary("timber").get())
            }
        }
    }
}