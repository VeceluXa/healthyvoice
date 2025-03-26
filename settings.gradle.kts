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
include(":common:core:presentation")
include(":common:uikit")
include(":common:resources")
include(":feature:root:presentation")
include(":feature:analyze:presentation")
include(":common:libs:recorder")
include(":data:common")
include(":feature:cut:presentation")
include(":feature:common:domain")
include(":common:core:domain")
include(":common:core:data")
include(":common:base")

include(":feature:record:domain")
include(":feature:record:data")
include(":feature:record:presentation")
