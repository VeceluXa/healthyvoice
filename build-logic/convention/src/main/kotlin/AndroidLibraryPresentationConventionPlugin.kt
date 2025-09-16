
import com.danilovfa.healthyvoice.libs
import org.gradle.api.Plugin import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryPresentationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.danilovfa.android.library.common.get().pluginId)
                apply(libs.plugins.danilovfa.android.library.compose.get().pluginId)
                apply(libs.plugins.kotlin.serialization.get().pluginId)
            }

            dependencies {
                add("implementation", project(":common:core:presentation"))
                add("implementation", project(":common:core:domain"))
                add("implementation", project(":common:base"))
                add("implementation", project(":common:uikit"))
                add("implementation", project(":common:resources"))

                add("implementation", libs.bundles.decompose)
                add("implementation", libs.bundles.mvikotlin)
                add("implementation", libs.bundles.koin)
                add("implementation", libs.kermit)
                add("implementation", libs.kotlin.serialization.json)
                add("implementation", libs.kotlin.datetime)
            }
        }
    }
}