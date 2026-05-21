package org.bradgravett.blindsphinx

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.bradgravett.blindsphinx.ui.LocalComposeWindow
import org.bradgravett.blindsphinx.ui.components.TitleBar

fun main() {
  // Override card data location for local development. Production uses ~/.blindsphinx/res/
  // (downloaded on first run).
  System.setProperty(
      "blindsphinx.forge.res",
      System.getProperty("user.home") + "/Desktop/Repos/forge/forge-gui/res",
  )
  System.setProperty("apple.awt.application.name", BuildConfig.APP_NAME)
  application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(),
        title = BuildConfig.APP_NAME,
        transparent = true,
        undecorated = true,
    ) {
      CompositionLocalProvider(LocalComposeWindow provides window) {
        MaterialTheme {
          ElevatedCard(Modifier.padding(16.dp)) {
            Column {
              WindowDraggableArea { TitleBar(::exitApplication) }
              MainScreen()
            }
          }
        }
      }
    }
  }
}
