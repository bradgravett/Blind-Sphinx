package org.bradgravett.blindsphinx.model

data class TcbMatch(
    val player1decklist: TcbDecklist = TcbDecklist(),
    val player2decklist: TcbDecklist = TcbDecklist(),
)
