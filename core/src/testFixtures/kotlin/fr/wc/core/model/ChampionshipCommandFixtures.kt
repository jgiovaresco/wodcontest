package fr.wc.core.model

import fr.wc.core.model.championship.ChampionshipId
import fr.wc.core.model.command.CreateChampionshipCommand
import fr.wc.core.model.command.RegisterAthleteCommand
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

fun aRegisterAthleteCommand(
    championshipId: ChampionshipId = ChampionshipId(faker.random.nextUUID()),
    athlete: Athlete = aMaleAthlete(),
    division: Division
): RegisterAthleteCommand {
    return RegisterAthleteCommand(championshipId, athlete, division)
}
