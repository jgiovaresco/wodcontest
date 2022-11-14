package fr.wc.core.model

import fr.wc.core.model.command.CreateChampionshipCommand
import fr.wc.utils.TimeProvider
import java.time.LocalDate

fun aCreateChampionshipCommand(
    name: String = faker.crossfit.competitions(),
    date: LocalDate = TimeProvider.today().plusDays(5),
    divisions: List<Division> =
        listOf(Division(Gender.Male, Level.RX), Division(Gender.Female, Level.RX))
): CreateChampionshipCommand {
    return CreateChampionshipCommand(name, date, divisions)
}
