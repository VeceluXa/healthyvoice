
import com.danilovfa.healthyvoice.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryDataConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.danilovfa.android.library.common.get().pluginId)
                apply(libs.plugins.kotlin.serialization.get().pluginId)
            }

            dependencies {
                add("implementation", project(":common:core:domain"))
                add("implementation", project(":common:core:data"))
                add("implementation", libs.bundles.koin)
                add("implementation", libs.kotlin.datetime)
                add("implementation", libs.kotlin.coroutines)
                add("implementation", libs.kotlin.serialization.json)
                add("implementation", libs.timber)
            }
        }
    }
}