package org.bradgravett.blindsphinx.model

import forge.CardStorageReader
import forge.ImageKeys
import forge.StaticData
import forge.util.Lang
import forge.util.Localizer
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.zip.ZipInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

private const val FORGE_RES_VERSION = "2.0.13"
private const val FORGE_RES_URL =
  "https://github.com/bradgravett/Blind-Sphinx/releases/download/forge-res-$FORGE_RES_VERSION/forge-res-$FORGE_RES_VERSION.zip"

class CardRepository {

  var cardNames: List<String> = emptyList()
    private set

  fun initialize(): Flow<StartupState> =
    flow {
        val resDir = resolveResDir()

        if (resDir == null) {
          emit(StartupState.Downloading(0f))
          downloadAndExtract { progress -> emit(StartupState.Downloading(progress)) }
        }

        emit(StartupState.Loading)

        val dir = resDir ?: cacheResDir()

        val customEditionsDir =
          File(cacheResDir().parentFile, "custom_editions").also { it.mkdirs() }

        ImageKeys.initializeDirs("", emptyMap(), "", "", "", "", "", "", "")
        Lang.createInstance("en-US")
        Localizer.getInstance()
          .initialize("en-US", "${dir.path}${File.separator}languages${File.separator}")

        val cardReader =
          CardStorageReader(
            "${dir.path}${File.separator}cardsfolder${File.separator}",
            CardStorageReader.ProgressObserver.emptyObserver,
            false,
          )
        StaticData(
          cardReader,
          null,
          "${dir.path}${File.separator}editions${File.separator}",
          customEditionsDir.path + File.separator,
          "${dir.path}${File.separator}blockdata${File.separator}",
          "Latest Art All Editions",
          true,
          false,
        )

        cardNames = StaticData.instance().commonCards.uniqueCards.map { it.name }.sorted()

        emit(StartupState.Ready(cardNames.size))
      }
      .flowOn(Dispatchers.IO)

  fun getSuggestions(query: String, limit: Int = 10): List<String> {
    if (query.isBlank()) return emptyList()
    return cardNames.filter { it.startsWith(query, ignoreCase = true) }.take(limit)
  }

  // Returns the res dir if already available (dev override or valid cache), null if download
  // needed.
  private fun resolveResDir(): File? {
    val override = System.getProperty("blindsphinx.forge.res")
    if (override != null) return File(override)

    val cached = cacheResDir()
    val versionFile = File(cached, ".version")
    if (versionFile.exists() && versionFile.readText().trim() == FORGE_RES_VERSION) return cached

    return null
  }

  private fun cacheResDir(): File =
    File(System.getProperty("user.home"), ".blindsphinx${File.separator}res")

  private suspend fun downloadAndExtract(onProgress: suspend (Float) -> Unit) {
    val destDir = cacheResDir()
    destDir.deleteRecursively()
    destDir.mkdirs()

    val client = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder(URI.create(FORGE_RES_URL)).GET().build()
    @Suppress("BlockingMethodInNonBlockingContext")
    val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())
    val contentLength = response.headers().firstValueAsLong("content-length").orElse(-1L)

    var bytesRead = 0L
    ZipInputStream(response.body().buffered()).use { zip ->
      generateSequence { zip.nextEntry }
        .forEach { entry ->
          val outFile = File(destDir, entry.name)
          if (entry.isDirectory) {
            outFile.mkdirs()
          } else {
            outFile.parentFile?.mkdirs()
            outFile.outputStream().use { out ->
              val buf = ByteArray(8192)
              var n: Int
              while (zip.read(buf).also { n = it } != -1) {
                out.write(buf, 0, n)
                bytesRead += n
                if (contentLength > 0) onProgress(bytesRead.toFloat() / contentLength)
              }
            }
          }
          zip.closeEntry()
        }
    }

    File(destDir, ".version").writeText(FORGE_RES_VERSION)
  }
}
