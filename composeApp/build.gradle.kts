import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val appName: String = providers.gradleProperty("app.name").get()

val generateBuildConfig by
  tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/buildconfig/kotlin")
    val name = appName
    outputs.dir(outputDir)
    doLast {
      val file = outputDir.get().asFile.resolve("org/bradgravett/blindsphinx/BuildConfig.kt")
      file.parentFile.mkdirs()
      file.writeText(
        """
            package org.bradgravett.blindsphinx

            object BuildConfig {
                const val APP_NAME = "$name"
            }
            """
          .trimIndent()
      )
    }
  }

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.composeHotReload)
}

kotlin {
  jvm()

  sourceSets {
    commonMain.dependencies {
      implementation(libs.compose.runtime)
      implementation(libs.compose.foundation)
      implementation(libs.compose.material3)
      implementation(libs.compose.ui)
      implementation(libs.compose.components.resources)
      implementation(libs.compose.uiToolingPreview)
      implementation(compose.materialIconsExtended)
      implementation(libs.androidx.lifecycle.viewmodelCompose)
      implementation(libs.androidx.lifecycle.runtimeCompose)
      implementation("org.jetbrains.compose.ui:ui-tooling:1.10.3")
    }
    commonTest.dependencies { implementation(libs.kotlin.test) }
    jvmMain {
      kotlin.srcDir(generateBuildConfig)
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation(libs.kotlinx.coroutinesSwing)
        implementation("forge:forge-game:2.0.13-SNAPSHOT")
        implementation("forge:forge-core:2.0.13-SNAPSHOT")
      }
    }
  }
}

compose.desktop {
  application {
    mainClass = "org.bradgravett.blindsphinx.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "org.bradgravett.blindsphinx"
      packageVersion = "1.0.0"
    }
  }
}
