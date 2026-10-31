rootProject.name = "Blind-Sphinx"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    // Local builds: Forge installed via `mvn install`. CI: GitHub Packages (credentials required).
    mavenLocal { mavenContent { includeGroup("forge") } }
    val gprUser = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
    val gprToken = providers.gradleProperty("gpr.token").orNull ?: System.getenv("GITHUB_TOKEN")
    if (gprUser != null && gprToken != null) {
      maven {
        url = uri("https://maven.pkg.github.com/bradgravett/Blind-Sphinx")
        credentials {
          username = gprUser
          password = gprToken
        }
        mavenContent { includeGroup("forge") }
      }
    }
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
  }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

include(":composeApp")
