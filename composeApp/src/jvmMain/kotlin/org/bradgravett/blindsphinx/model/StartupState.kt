package org.bradgravett.blindsphinx.model

sealed class StartupState {
    data object Loading : StartupState()
    data class Downloading(val progress: Float) : StartupState()
    data class Ready(val cardCount: Int) : StartupState()
    data class Error(val cause: Throwable) : StartupState()
}