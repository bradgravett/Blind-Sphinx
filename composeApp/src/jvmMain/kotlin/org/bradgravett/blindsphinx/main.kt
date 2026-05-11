package org.bradgravett.blindsphinx

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
  System.setProperty("apple.awt.application.name", BuildConfig.APP_NAME)
  application {
    Window(
        onCloseRequest = ::exitApplication,
        title = BuildConfig.APP_NAME,
    ) {
      App()
    }
  }
}
