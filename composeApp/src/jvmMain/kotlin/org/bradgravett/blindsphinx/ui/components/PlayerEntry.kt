package org.bradgravett.blindsphinx.ui.components

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.bradgravett.blindsphinx.ui.dimensions

@Composable
fun PlayerEntry(getSuggestions: (String) -> List<String> = { emptyList() }) {
  val playerName = rememberTextFieldState()
  val (quarterMargin, halfMargin, standardMargin, outerMargin) = MaterialTheme.dimensions

  OutlinedCard {
    SimpleTable(
      SimpleTableDirection.ColumnsThenRows,
      content =
        listOf(
          listOf(
            { Text("Player Name", maxLines = 1, softWrap = false) },
            { Text("Card 1", maxLines = 1, softWrap = false) },
            { Text("Card 2", maxLines = 1, softWrap = false) },
            { Text("Card 3", maxLines = 1, softWrap = false) },
          ),
          listOf(
            { TextField(playerName, Modifier.widthIn(240.dp), label = { Text("Player Name") }) },
            { CardAutocompleteField(getSuggestions) },
            { CardAutocompleteField(getSuggestions) },
            { CardAutocompleteField(getSuggestions) },
          ),
        ),
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardAutocompleteField(getSuggestions: (String) -> List<String>) {
  var query by remember { mutableStateOf("") }
  var expanded by remember { mutableStateOf(false) }
  val suggestions = remember(query) { getSuggestions(query) }

  ExposedDropdownMenuBox(
    expanded = expanded && suggestions.isNotEmpty(),
    onExpandedChange = { expanded = it },
    modifier = Modifier.widthIn(max = 240.dp),
  ) {
    TextField(
      value = query,
      onValueChange = {
        query = it
        expanded = true
      },
      singleLine = true,
      trailingIcon = {
        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && suggestions.isNotEmpty())
      },
      modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
    )
    ExposedDropdownMenu(
      expanded = expanded && suggestions.isNotEmpty(),
      onDismissRequest = { expanded = false },
    ) {
      suggestions.forEach { name ->
        DropdownMenuItem(
          text = { Text(name) },
          onClick = {
            query = name
            expanded = false
          },
        )
      }
    }
  }
}

@Composable
@Preview
private fun PlayerEntryPreview() {
  PlayerEntry { emptyList() }
}
