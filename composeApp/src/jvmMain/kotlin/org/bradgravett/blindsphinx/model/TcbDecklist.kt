package org.bradgravett.blindsphinx.model

import forge.item.PaperCard

data class TcbDecklist(
    var playerName: String = "",
    var card1: PaperCard = PaperCard.FAKE_CARD,
    var card2: PaperCard = PaperCard.FAKE_CARD,
    var card3: PaperCard = PaperCard.FAKE_CARD,
)
