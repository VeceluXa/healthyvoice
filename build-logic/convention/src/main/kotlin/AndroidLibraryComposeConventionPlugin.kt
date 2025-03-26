
import com.android.build.gradle.LibraryExtension
import com.danilovfa.healthyvoice.configureAndroidCompose
import com.danilovfa.healthyvoice.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)

            dependencies {
                add("implementation", libs.findBundle("compose").get())
                add("debugImplementation", libs.findLibrary("compose.ui.tooling").get())
            }
        }
    }

}