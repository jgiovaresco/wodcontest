package fr.wc.core.model

fun maleRXDivision(): Division {
  return Division(Gender.Male, Level.RX)
}

fun femaleRXDivision(): Division {
  return Division(Gender.Female, Level.RX)
}

fun maleScaledDivision(): Division {
  return Division(Gender.Male, Level.SCALED)
}

fun femaleScaledDivision(): Division {
  return Division(Gender.Female, Level.SCALED)
}
