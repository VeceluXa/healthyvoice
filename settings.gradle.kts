pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Healthy Voice"
include(":app")

include(":common:core:data")
include(":common:core:domain")
include(":common:core:presentation")

include(":common:base")
include(":common:uikit")
include(":common:resources")

include(":common:libs:recorder")

include(":feature:root:presentation")
include(":feature:common:domain")
include(":feature:analyze:presentation")
include(":feature:record:domain")
include(":feature:record:data")
include(":feature:record:presentation")
include(":feature:cut:presentation")
