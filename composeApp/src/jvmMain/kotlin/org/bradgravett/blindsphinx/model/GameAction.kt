package org.bradgravett.blindsphinx.model

import forge.game.spellability.SpellAbility

sealed class GameAction {
  data class CastSpell(val spellAbility: SpellAbility) : GameAction()

  data class ActivateAbility(val spellAbility: SpellAbility) : GameAction()

  data object PassPriority : GameAction()
}
