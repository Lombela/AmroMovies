pluginManagement {
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

rootProject.name = "AbnaMoviesA"
include(":app")
include(":core:ui")
include(":core:util")
include(":domain")
include(":data")
include(":feature:trending")
include(":feature:detail")
include(":feature:popular")
include(":feature:actors")
include(":feature:library")
 
