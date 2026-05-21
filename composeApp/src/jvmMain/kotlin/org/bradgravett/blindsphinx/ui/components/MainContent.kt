package org.bradgravett.blindsphinx.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.bradgravett.blindsphinx.ui.dimensions
import org.bradgravett.blindsphinx.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(vm: MainViewModel) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val (quarterMargin, halfMargin, standardMargin, outerMargin) = MaterialTheme.dimensions

  Column {
    PrimaryTabRow(selectedTab) {
      Row(horizontalArrangement = Arrangement.Start) {
        Tab(selectedTab == 0, text = { Text("Single Match") }, onClick = { selectedTab = 0 })
      }
    }
    Spacer(Modifier.height(halfMargin))
    Row {
      PlayerEntry(getSuggestions = vm::getSuggestions)
      Spacer(Modifier.width(halfMargin))
      PlayerEntry(getSuggestions = vm::getSuggestions)
    }
  }
}

@Composable
@Preview(heightDp = 360, widthDp = 640)
private fun MainContentPreview() {
  ElevatedCard { MainContent(MainViewModel()) }
}
