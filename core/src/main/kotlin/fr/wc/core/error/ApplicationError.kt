package fr.wc.core.error

import fr.wc.core.model.Athlete
import fr.wc.core.model.Division
import fr.wc.core.model.championship.ChampionshipId

sealed class ApplicationError {
    data class ChampionshipNotFound(val id: ChampionshipId) : ApplicationError()

    // CreateChampionshipError
    object EmptyChampionshipName : ApplicationError()
    object ScheduledInPastChampionship : ApplicationError()
    data class AlreadyExistingChampionship(val name: String) : ApplicationError()

    // RegisterAthleteError
    data class UnavailableDivision(val division: Division) : ApplicationError()
    data class IncorrectDivision(val division: Division, val athlete: Athlete) : ApplicationError()

    // Starting
    object NotEnoughAthlete : ApplicationError()
    object NoEvent : ApplicationError()
}
