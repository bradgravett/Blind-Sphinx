plugins {
  // this is necessary to avoid the plugins to be loaded multiple times
  // in each subproject's classloader
  alias(libs.plugins.composeHotReload) apply false
  alias(libs.plugins.composeMultiplatform) apply false
  alias(libs.plugins.composeCompiler) apply false
  alias(libs.plugins.kotlinMultiplatform) apply false
  alias(libs.plugins.spotless)
}

spotless {
  kotlin {
    target("**/*.kt")
    targetExclude("**/build/**/*.kt")
    ktfmt("0.54").googleStyle()
  }
  kotlinGradle {
    target("**/*.kts")
    targetExclude("**/build/**/*.kts")
    ktfmt("0.54").googleStyle()
  }
}
