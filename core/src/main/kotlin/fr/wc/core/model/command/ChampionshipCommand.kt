package fr.wc.core.model.command

import fr.wc.core.model.Division
import java.time.LocalDate

sealed class ChampionshipCommand : Command

data class CreateChampionshipCommand(
    val name: String,
    val date: LocalDate,
    val divisions: List<Division>
) : ChampionshipCommand()
