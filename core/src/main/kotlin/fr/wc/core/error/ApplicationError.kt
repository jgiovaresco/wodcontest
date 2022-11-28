package fr.wc.core.error

import arrow.core.NonEmptyList
import fr.wc.core.InvalidField
import fr.wc.core.model.*


sealed interface ApplicationError

sealed interface ValidationError : ApplicationError
data class IncorrectInput(val errors: NonEmptyList<InvalidField>) : ValidationError

data class ChampionshipNotFound(val id: ChampionshipId) : ApplicationError
data class EventNotFound(val id: EventId) : ApplicationError
data class AthleteNotFound(val id: AthleteId) : ApplicationError

// CreateChampionshipError
data class AlreadyExistingChampionship(val name: String) : ApplicationError

// Starting
sealed interface ChampionshipStartError : ApplicationError
object NotEnoughAthlete : ChampionshipStartError
object NoEvent : ChampionshipStartError

// RegisterScoreError
data class IncorrectScoreType(val event: Event, val score: Score) : ApplicationError
