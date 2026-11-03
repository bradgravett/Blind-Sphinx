package org.bradgravett.blindsphinx.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import forge.item.PaperCard
import org.bradgravett.blindsphinx.model.TcbDecklist
import org.bradgravett.blindsphinx.ui.dimensions

@Composable
fun PlayerEntry(
    getSuggestions: (String) -> List<String>,
    getCard: (String) -> PaperCard,
    setDecklist: (TcbDecklist) -> Unit,
) {
  val playerName = rememberTextFieldState()
  val (quarterMargin, halfMargin, standardMargin, outerMargin) = MaterialTheme.dimensions
  var decklist by remember { mutableStateOf(TcbDecklist()) }

  LaunchedEffect(decklist) {
    setDecklist(decklist)
  }

  OutlinedCard {
    Column(Modifier.width(IntrinsicSize.Min).padding(16.dp), verticalArrangement = spacedBy(halfMargin)) {
      Row(
          Modifier.fillMaxWidth(),
          Arrangement.SpaceBetween,
          Alignment.CenterVertically,
      ) {
        Text("Player\nName")
        Spacer(Modifier.weight(1f))
        TextField(playerName, Modifier.widthIn(240.dp), label = { Text("Player Name") })
      }
      Row(
          Modifier.fillMaxWidth(),
          Arrangement.SpaceBetween,
          Alignment.CenterVertically,
      ) {
        Text("Card 1", maxLines = 1, softWrap = false)
          Spacer(Modifier.weight(1f))

        CardAutocompleteField(getSuggestions, decklist.card1) {
          decklist.card1 = getCard(it)
        }
      }
      Row(
          Modifier.fillMaxWidth(),
          Arrangement.SpaceBetween,
          Alignment.CenterVertically,
      ) {
        Text("Card 2", maxLines = 1, softWrap = false)
          Spacer(Modifier.weight(1f))

        CardAutocompleteField(getSuggestions, decklist.card2) {
          decklist.card2 = getCard(it)
        }
      }
      Row(
          Modifier.fillMaxWidth(),
          Arrangement.SpaceBetween,
          Alignment.CenterVertically,
      ) {
        Text("Card 3", maxLines = 1, softWrap = false)
          Spacer(Modifier.weight(1f))

        CardAutocompleteField(getSuggestions, decklist.card3) {
          decklist.card3 = getCard(it)
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardAutocompleteField(
    getSuggestions: (String) -> List<String>,
    selectedCard: PaperCard,
    selectCard: (String) -> Unit,
) {
  var query by remember { mutableStateOf(selectedCard.cardName) }
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
          selectCard(it)
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
  PlayerEntry(
      getSuggestions = { emptyList() },
      getCard = { PaperCard.FAKE_CARD },
      setDecklist = {},
  )
}
