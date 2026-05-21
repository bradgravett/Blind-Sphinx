package org.bradgravett.blindsphinx.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.bradgravett.blindsphinx.ui.LocalComposeWindow

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalComposeUiApi::class)
@Composable
fun TitleBar(closeWindow: () -> Unit) {
  val window = LocalComposeWindow.current
  var visible by remember { mutableStateOf(false) }
  var pinned by remember { mutableStateOf(false) }
  Box(Modifier.fillMaxWidth().height(32.dp)) {
    Box(
        Modifier.fillMaxSize()
            .onPointerEvent(PointerEventType.Enter, onEvent = { visible = true })
            .onPointerEvent(PointerEventType.Exit, onEvent = { visible = false })
    ) {
      AnimatedVisibility(
          visible,
          enter = fadeIn() + expandVertically(),
          exit = shrinkVertically() + fadeOut(),
      ) {
        Box {
          Box(Modifier.fillMaxSize().alpha(.1f).background(Color.Black))
          Row(Modifier.fillMaxSize(), Arrangement.Start, Alignment.CenterVertically) {
            IconButton(closeWindow) { Icon(Icons.Default.Close, null) }
            IconButton(onClick = {
              pinned = !pinned
              window?.isAlwaysOnTop = pinned
            }) {
              Icon(if (pinned) Icons.Default.PushPin else Icons.Outlined.PushPin, null)
            }
          }
          Row(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterVertically) {
            Icon(Icons.Default.Menu, null)
          }
        }
      }
    }
  }
}

@Composable
@Preview
private fun TitleBarPreview() {
  TitleBar {}
}
