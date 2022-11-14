package fr.wc.core.model

fun aMaleAthlete(
  id: AthleteId = AthleteId(faker.random.nextUUID()),
  name: String = faker.crossfit.maleAthletes(),
): Athlete {
  return Athlete(id, name, Gender.Male)
}

fun aFemaleAthlete(
  id: AthleteId = AthleteId(faker.random.nextUUID()),
  name: String = faker.crossfit.femaleAthletes(),
): Athlete {
  return Athlete(id, name, Gender.Female)
}
