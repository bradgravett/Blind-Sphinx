package org.bradgravett.blindsphinx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed interface SimpleTableDirection {
  data object ColumnsThenRows : SimpleTableDirection

  data object RowsThenColumns : SimpleTableDirection
}

@Composable
fun SimpleTable(
    direction: SimpleTableDirection,
    modifier: Modifier = Modifier,
    content: List<List<@Composable () -> Unit>>,
    tablePadding: Dp = 4.dp,
    cellPadding: Dp = 4.dp,
) {
  if (content.isEmpty() || content.first().isEmpty()) return

  Box(
      modifier =
          modifier.background(color = MaterialTheme.colorScheme.background).padding(tablePadding)
  ) {
    when (direction) {
      SimpleTableDirection.ColumnsThenRows -> {
        // content[col][row]. Primary visual axis: rows (top-to-bottom).
        val numCols = content.size
        val numRows = content.maxOf { it.size }
        TableLayout(
            numCols = numCols,
            numRows = numRows,

            // Emit cells in column-major order so measurables[col * numRows + row] = content[col][row].
            cellContent = {
              (0 until numCols).forEach { col ->
                (0 until numRows).forEach { row ->
                  Box(Modifier.padding(cellPadding)) { content[col].getOrNull(row)?.invoke() }
                }
              }
            },
            cellIndex = { col, row -> col * numRows + row },
        )
      }
      SimpleTableDirection.RowsThenColumns -> {
        // content[row][col]. Primary visual axis: columns (left-to-right).
        val numRows = content.size
        val numCols = content.maxOf { it.size }
        TableLayout(
            numCols = numCols,
            numRows = numRows,

            // Emit cells in row-major order so measurables[row * numCols + col] = content[row][col].
            cellContent = {
              (0 until numRows).forEach { row ->
                (0 until numCols).forEach { col ->
                  Box(Modifier.padding(cellPadding)) { content[row].getOrNull(col)?.invoke() }
                }
              }
            },
            cellIndex = { col, row -> row * numCols + col },
        )
      }
    }
  }
}

@Composable
private fun TableLayout(
    numCols: Int,
    numRows: Int,
    cellContent: @Composable () -> Unit,
    cellIndex: (col: Int, row: Int) -> Int,
) {
  Layout(content = cellContent) { measurables, constraints ->
    // Pre-pass: intrinsic widths determine each column's required width.
    val colWidths = IntArray(numCols) { col ->
      (0 until numRows).maxOf { row ->
        measurables[cellIndex(col, row)].maxIntrinsicWidth(constraints.maxHeight)
      }
    }

    // Single measure pass with fixed column widths.
    val placeables = Array(numCols) { col ->
      Array(numRows) { row ->
        measurables[cellIndex(col, row)].measure(Constraints.fixedWidth(colWidths[col]))
      }
    }

    val rowHeights = IntArray(numRows) { row ->
      (0 until numCols).maxOf { col -> placeables[col][row].height }
    }

    val colOffsets = IntArray(numCols).also { for (i in 1 until numCols) it[i] = it[i - 1] + colWidths[i - 1] }
    val rowOffsets = IntArray(numRows).also { for (i in 1 until numRows) it[i] = it[i - 1] + rowHeights[i - 1] }

    layout(
        width = colWidths.sum().coerceAtMost(constraints.maxWidth),
        height = rowHeights.sum(),
    ) {
      (0 until numCols).forEach { col ->
        (0 until numRows).forEach { row ->
          placeables[col][row].placeRelative(colOffsets[col], rowOffsets[row])
        }
      }
    }
  }
}

private fun previewContent(): List<List<@Composable () -> Unit>> =
    listOf(
        listOf(
            { Text(text = "Alpha One") },
            { Text(text = "Beta One") },
            { Text(text = "Gamma One") },
            { Text(text = "Delta One") },
            ),
        listOf(
            { Text(text = "Alpha Two") },
            { Text(text = "Beta Two") },
            { Text(text = "Gamma Two") },
            { Text(text = "Delta Two") },
            { Text(text = "Epsilon Two") },
            ),
        listOf(
            { Text(text = "Alpha Three") },
            { Text(text = "Beta Three") },
            { Text(text = "Gamma Three") },
        ),
    )

@Composable
@Preview
private fun SimpleTablePreviewColumnsThenRows() {
  Column {
    SimpleTable(
        direction = SimpleTableDirection.ColumnsThenRows,
        content = previewContent(),
    )
    HorizontalDivider()
    SimpleTable(
        direction = SimpleTableDirection.RowsThenColumns,
        content = previewContent(),
    )
  }
}
