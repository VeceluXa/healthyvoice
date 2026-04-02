import com.danilovfa.healthyvoice.configureKotlinJvm
import com.danilovfa.healthyvoice.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.java.library.get().pluginId)
                apply(libs.plugins.jetbrains.kotlin.jvm.get().pluginId)
            }

            configureKotlinJvm()

            dependencies {
            }
        }
    }
}