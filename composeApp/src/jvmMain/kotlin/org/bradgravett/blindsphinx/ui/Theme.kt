package org.bradgravett.blindsphinx.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.awt.Window

val LocalComposeWindow = staticCompositionLocalOf<Window?> { null }

data class Dimensions(
  val quarterMargin: Dp,
  val halfMargin: Dp,
  val standardMargin: Dp,
  val outerMargin: Dp,
  val horizontalRuleWidth: Dp,
  val smallestTouchTarget: Dp,
)

val MaterialTheme.dimensions
  @Composable
  @ReadOnlyComposable
  get() =
    Dimensions(
      quarterMargin = 4.dp,
      halfMargin = 8.dp,
      standardMargin = 16.dp,
      outerMargin = 24.dp,
      horizontalRuleWidth = 1.dp,
      smallestTouchTarget = 48.dp,
    )
