import java.io.ByteArrayOutputStream
import javax.inject.Inject
import org.gradle.process.ExecOperations
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val appName: String = providers.gradleProperty("app.name").get()

// Forge is a git submodule at ./forge, built with Maven and installed into ~/.m2.
val forgeDir: Directory = rootProject.layout.projectDirectory.dir("forge")

if (!forgeDir.file("pom.xml").asFile.exists()) {
  throw GradleException("Forge submodule missing. Run: git submodule update --init")
}

val forgeVersion: String =
  providers.fileContents(forgeDir.file("pom.xml")).asText.get().let { pom ->
    // Release tags hardcode the project version; master uses ${revision}, built from properties.
    val version = Regex("<version>(.+?)</version>").find(pom)!!.groupValues[1]
    if (version != "\${revision}") return@let version
    val code = Regex("<versionCode>(.+?)</versionCode>").find(pom)!!.groupValues[1]
    val snapshot = Regex("<snapshotName>(.*?)</snapshotName>").find(pom)?.groupValues?.get(1)
    code + snapshot.orEmpty()
  }

val forgeCommit: Provider<String> =
  providers
    .exec { commandLine("git", "-C", forgeDir.asFile.path, "rev-parse", "HEAD") }
    .standardOutput
    .asText
    .map { it.trim() }

val forgeGameJar =
  File(
    System.getProperty("user.home"),
    ".m2/repository/forge/forge-game/$forgeVersion/forge-game-$forgeVersion.jar",
  )

val installForge by
  tasks.registering(Exec::class) {
    group = "forge"
    description = "Builds the Forge submodule and installs forge-game into ~/.m2."
    // Search PATH explicitly: the Gradle daemon (or an IDE launched from the Dock) may not have
    // Homebrew's bin directory on its own PATH.
    val mvnName = if (System.getProperty("os.name").startsWith("Windows")) "mvn.cmd" else "mvn"
    val searchDirs =
      listOfNotNull(providers.environmentVariable("MAVEN_HOME").orNull?.let { "$it/bin" }) +
        providers.environmentVariable("PATH").getOrElse("").split(File.pathSeparator) +
        listOf("/opt/homebrew/bin", "/usr/local/bin")
    val mvn = searchDirs.map { File(it, mvnName) }.firstOrNull { it.canExecute() }?.path ?: mvnName
    workingDir(forgeDir)
    // flatten:flatten resolves Forge's ${revision} version so Gradle can read the installed POMs.
    commandLine(mvn, "-q", "-pl", "forge-game", "-am", "flatten:flatten", "install", "-DskipTests")
    inputs.property("forgeCommit", forgeCommit)
    outputs.file(forgeGameJar)
  }

// Moves the submodule to Forge's newest release tag (forge-X.Y.Z). Commit the result.
abstract class UpdateForge @Inject constructor(private val exec: ExecOperations) : DefaultTask() {
  @get:Internal abstract val forgeDir: DirectoryProperty

  @TaskAction
  fun update() {
    val dir = forgeDir.get().asFile.path
    val tags = ByteArrayOutputStream()
    exec.exec {
      commandLine("git", "-C", dir, "ls-remote", "--tags", "--refs", "origin", "forge-*")
      standardOutput = tags
    }
    val versionPattern = Regex("""refs/tags/forge-(\d+(?:\.\d+)*)$""")
    val latest =
      tags
        .toString()
        .lines()
        .mapNotNull { versionPattern.find(it.trim())?.groupValues?.get(1) }
        .maxWithOrNull { a, b ->
          val (x, y) = a.split('.').map(String::toInt) to b.split('.').map(String::toInt)
          (0 until maxOf(x.size, y.size))
            .map { (x.getOrNull(it) ?: 0).compareTo(y.getOrNull(it) ?: 0) }
            .firstOrNull { it != 0 } ?: 0
        } ?: throw GradleException("No forge-* release tags found")
    val tag = "forge-$latest"
    exec.exec { commandLine("git", "-C", dir, "fetch", "-q", "--depth", "1", "origin", "tag", tag) }
    exec.exec { commandLine("git", "-C", dir, "checkout", "-q", tag) }
    logger.lifecycle("Forge submodule now at $tag. Run ./gradlew installForge, then commit forge.")
  }
}

tasks.register<UpdateForge>("updateForge") {
  group = "forge"
  description = "Moves the Forge submodule to the newest forge-* release tag."
  forgeDir.set(rootProject.layout.projectDirectory.dir("forge"))
}

// Dependency resolution happens before any task runs, so Forge must be installed beforehand.
val forgeTasks = listOf("installForge", "updateForge")

if (
  !forgeGameJar.exists() &&
    gradle.startParameter.taskNames.none { task -> forgeTasks.any { task.endsWith(it) } }
) {
  throw GradleException("Forge $forgeVersion is not installed. Run: ./gradlew installForge")
}

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
  // Match Compose Hot Reload's JetBrains Runtime (21) and CI.
  jvmToolchain(21)
  jvm()

  sourceSets {
    commonMain.dependencies {
      implementation(libs.compose.runtime)
      implementation(libs.compose.foundation)
      implementation(libs.compose.material3)
      implementation(libs.compose.ui)
      implementation(libs.compose.components.resources)
      implementation(libs.compose.uiToolingPreview)
      implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
      implementation(libs.androidx.lifecycle.viewmodelCompose)
      implementation(libs.androidx.lifecycle.runtimeCompose)
      implementation("org.jetbrains.compose.ui:ui-tooling:1.10.3")
      implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.10.0")
    }
    commonTest.dependencies { implementation(libs.kotlin.test) }
    jvmMain {
      kotlin.srcDir(generateBuildConfig)
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation(libs.kotlinx.coroutinesSwing)
        implementation("forge:forge-game:$forgeVersion")
        implementation("forge:forge-core:$forgeVersion")
      }
    }
  }
}

// Dev runs read card data straight from the submodule. Packaged builds download it instead.
tasks.withType<JavaExec>().configureEach {
  systemProperty("blindsphinx.forge.res", forgeDir.dir("forge-gui/res").asFile.absolutePath)
}

compose.desktop {
  application {
    mainClass = "org.bradgravett.blindsphinx.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "org.bradgravett.blindsphinx"
      packageVersion = "1.0.0"
      macOS {
        iconFile.set(project.file("src/jvmMain/composeResources/drawable/app-icon-mac.icns"))
      }
      windows {
        iconFile.set(project.file("src/jvmMain/composeResources/drawable/app-icon-windows.ico"))
      }
      linux {
        iconFile.set(project.file("src/jvmMain/composeResources/drawable/app-icon-linux.png"))
      }
    }
  }
}
