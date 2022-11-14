package fr.wc.core.model.championship

import fr.wc.core.model.*
import fr.wc.utils.TimeProvider
import java.time.LocalDate

class ChampionshipBuilder(id: String, name: String, date: LocalDate, division: List<Division>) {
  var championship: Championship =
    Championship.createdChampionship(id, name, date, division)

  companion object Builder {
    fun aChampionship(
      id: String = faker.random.nextUUID(),
      name: String = faker.crossfit.competitions(),
      date: LocalDate = TimeProvider.today().plusDays(5),
      divisions: List<Division> = listOf(maleRXDivision(), femaleRXDivision()),
    ): ChampionshipBuilder {
      return ChampionshipBuilder(id, name, date, divisions)
    }
  }

  fun withAthletes(vararg athletes: Pair<Division, Athlete>): ChampionshipBuilder {
    return withAthletes(athletes.toList())
  }

  fun withAthletes(
    athletes: List<Pair<Division, Athlete>> =
      listOf(
        Pair(maleRXDivision(), aMaleAthlete()),
        Pair(maleRXDivision(), aMaleAthlete()),
        Pair(femaleRXDivision(), aFemaleAthlete()),
        Pair(femaleRXDivision(), aFemaleAthlete()),
      )
  ): ChampionshipBuilder {

    championship = Championship.registeredAthletes.registrations.set(championship, athletes)
    return this
  }


  fun created(): Championship {
    return championship
  }
}
