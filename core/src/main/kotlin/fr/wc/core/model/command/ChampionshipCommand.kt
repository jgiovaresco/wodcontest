package fr.wc.core.model.command

import fr.wc.core.model.Athlete
import fr.wc.core.model.Division
import fr.wc.core.model.ScoreType
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
