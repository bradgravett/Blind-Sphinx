Please keep responses concise and minimal, in the style of a Star Trek ship computer. Bulleted lists, numbered lists, and tables are preferred when appropriate. Please avoid beginning responses with affirmations or congratulatory statements. Please also avoid asking leading questions at the end of responses.



**BlindSphinx — Project Summary for Claude Code**

**What it is**
A deterministic Magic: The Gathering match solver for the 3 Card Blind format. Two players submit 3-card "decks"; the solver computes the optimal outcome (win/loss/draw) for both play orders without human input.

**Repos**
- `blindsphinx` — KMP desktop app (Gradle, Kotlin, Compose Multiplatform)
- Forge cloned locally, `forge-game` built and installed to `~/.m2` via Maven

**Tech stack**
- Kotlin Multiplatform, Compose Multiplatform (desktop target)
- `org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose` for MVVM
- `forge-game` (Card-Forge/forge) as rules engine dependency via `mavenLocal()`
- `jpackage` + `jlink` for native distribution (Windows/Mac/Linux)
- Package name: `org.bradgravett.blindsphinx`

**Architecture**
MVVM. Two clean layers:
- **Rules layer** — delegated to `forge-game`: game state, legal move generation, action execution, APNAP, phase/priority management
- **Solver layer** — BlindSphinx code: negamax + alpha-beta pruning, transposition table (Zobrist hashing), loop detector, result reporter

**Solver design**
- Negamax with alpha-beta pruning — exhaustive perfect-information search
- Terminal states only: win/loss/draw (no heuristic scoring needed)
- `RuleSet` interface defined early for future alternate ruleset support
- `StateHash` via Zobrist XOR for O(1) transposition table lookups
- Loop detector is per-search-path `Set<StateHash>`, distinct from transposition table

**3CB rules that affect solver design**
- No library/deck; all 3 cards start in hand
- Perfect information; all decisions public
- Randomness resolves opponent-favorable (no chance nodes in search tree)
- Intentional loops prohibited (MTG rule 104.4b)
- Best-of-two per matchup (each player goes first once)

**Key data model**
```kotlin
interface RuleSet {
    val name: String
    val deckConstraints: DeckConstraints
    val gameStartConditions: GameStartConditions
    val randomnessResolution: RandomnessPolicy
    val drawConditions: DrawConditions
    val winConditions: WinConditions
}
```

**Todos flagged for future discussion**
- Open source license (GPL-3.0 inheritance from Forge)
- CI/CD (cross-platform binaries, Mac notarization)
- Testing framework (3CB match history as solver oracle)
- Multithreading (negamax parallelism, Forge state cloneability, coroutine dispatchers)