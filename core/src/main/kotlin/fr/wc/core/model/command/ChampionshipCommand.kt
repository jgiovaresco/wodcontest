package fr.wc.core.model.command

import fr.wc.core.model.*
import fr.wc.core.model.championship.ChampionshipId
import java.time.LocalDate

sealed class ChampionshipCommand : Command

data class CreateChampionshipCommand(
    val name: String,
    val date: LocalDate,
    val divisions: List<Division>
) : ChampionshipCommand()

data class RegisterAthleteCommand(
    val championshipId: ChampionshipId,
    val athlete: Athlete,
    val division: Division
) : ChampionshipCommand()

data class RegisterEventCommand(
    val championshipId: ChampionshipId,
    val name: String,
    val description: String,
    val scoreType: ScoreType,
) : ChampionshipCommand()

data class StartChampionshipCommand(
    val championshipId: ChampionshipId,
) : ChampionshipCommand()

data class RegisterScoreCommand(
    val championshipId: ChampionshipId,
    val eventId: EventId,
    val athleteId: AthleteId,
    val score: Score
) : ChampionshipCommand()
