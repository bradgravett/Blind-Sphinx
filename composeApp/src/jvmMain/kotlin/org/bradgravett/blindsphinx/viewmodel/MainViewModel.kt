package org.bradgravett.blindsphinx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import forge.game.zone.ZoneType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.bradgravett.blindsphinx.model.BoardState
import org.bradgravett.blindsphinx.model.CardRepository
import org.bradgravett.blindsphinx.model.GameAction
import org.bradgravett.blindsphinx.model.StartupState

class MainViewModel : ViewModel() {

    private val cardRepository = CardRepository()

    private val _startupState = MutableStateFlow<StartupState>(StartupState.Loading)
    val startupState: StateFlow<StartupState> = _startupState.asStateFlow()

    init {
        viewModelScope.launch {
            cardRepository.initialize()
                .catch { e -> emit(StartupState.Error(e)) }
                .collect { state -> _startupState.value = state }
        }
    }

    fun getSuggestions(query: String): List<String> = cardRepository.getSuggestions(query)

    fun getValidActions(gameState: BoardState): List<GameAction> {
        val game = gameState.game
        val player = game.phaseHandler.priorityPlayer ?: return listOf(GameAction.PassPriority)

        val actions = mutableListOf<GameAction>()

        listOf(ZoneType.Hand, ZoneType.Battlefield).forEach { zoneType ->
            player.getCardsIn(zoneType).forEach { card ->
                card.spellAbilities.forEach { ability ->
                    if (ability.restrictions.canPlay(card, ability)) {
                        val action =
                            if (ability.isSpell) {
                                GameAction.CastSpell(ability)
                            } else {
                                GameAction.ActivateAbility(ability)
                            }
                        actions.add(action)
                    }
                }
            }
        }

        actions.add(GameAction.PassPriority)
        return actions
    }
}
