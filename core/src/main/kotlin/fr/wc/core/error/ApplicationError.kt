package fr.wc.core.error

import fr.wc.core.model.*
import fr.wc.core.model.championship.ChampionshipId


sealed interface ApplicationError

data class ChampionshipNotFound(val id: ChampionshipId) : ApplicationError
data class EventNotFound(val id: EventId) : ApplicationError
data class AthleteNotFound(val id: AthleteId) : ApplicationError

// CreateChampionshipError
object EmptyChampionshipName : ApplicationError
object ScheduledInPastChampionship : ApplicationError
data class AlreadyExistingChampionship(val name: String) : ApplicationError

// RegisterAthleteError
data class UnavailableDivision(val division: Division) : ApplicationError
data class IncorrectDivision(val division: Division, val athlete: Athlete) : ApplicationError

// Starting
object NotEnoughAthlete : ApplicationError
object NoEvent : ApplicationError

// RegisterScoreError
data class IncorrectScoreType(val event: Event, val score: Score) : ApplicationError
