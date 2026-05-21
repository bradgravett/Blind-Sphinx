package org.bradgravett.blindsphinx

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import org.bradgravett.blindsphinx.model.StartupState
import org.bradgravett.blindsphinx.ui.components.MainContent
import org.bradgravett.blindsphinx.viewmodel.MainViewModel

@Composable
@Preview
fun MainScreen(
    vm: MainViewModel = viewModel(factory = viewModelFactory { initializer { MainViewModel() } })) {
  val startupState by vm.startupState.collectAsStateWithLifecycle()


        when (val state = startupState) {
          is StartupState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Loading card database...")
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator()
              }
            }
          }

          is StartupState.Downloading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Downloading card database...")
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(progress = { state.progress })
              }
            }
          }

          is StartupState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Text("Failed to load card database: ${state.cause.message}")
            }
          }

          is StartupState.Ready -> {
            MainContent(vm)
          }
        }
      }
