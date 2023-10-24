package fr.wc.core.model

import fr.wc.core.model.command.*
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

fun aRegisterEventCommand(
    championshipId: ChampionshipId = ChampionshipId(faker.random.nextUUID()),
    name: String = faker.crossfit.girlWorkouts(),
    description: String = "description of $name",
    scoreType: ScoreType = faker.random.nextEnum()
): RegisterEventCommand = RegisterEventCommand(championshipId, name, description, scoreType)

fun aStartChampionshipCommand(
    championshipId: ChampionshipId = ChampionshipId(faker.random.nextUUID()),
): StartChampionshipCommand = StartChampionshipCommand(championshipId)

fun aRegisterScoreCommand(
    championshipId: ChampionshipId = ChampionshipId(faker.random.nextUUID()),
    eventId: EventId = EventId(faker.random.nextUUID()),
    athleteId: AthleteId = AthleteId(faker.random.nextUUID()),
    score: Score = aTimeScore(),
) = RegisterScoreCommand(championshipId, eventId, athleteId, score)
