import com.danilovfa.healthyvoice.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KotlinLibraryDomainConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.danilovfa.kotlin.library.common.get().pluginId)
                apply(libs.plugins.kotlin.serialization.get().pluginId)
            }

            dependencies {
                add("implementation", project(":common:core:domain"))

                add("implementation", libs.kotlin.coroutines)
                add("implementation", libs.kotlin.serialization.json)
                add("implementation", libs.kotlin.datetime)
            }
        }
    }
}