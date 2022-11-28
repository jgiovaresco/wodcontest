package fr.wc.core.model

import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.nonEmptyListOf
import arrow.core.validNel
import fr.wc.core.InvalidDivision
import fr.wc.core.toInvalidField

data class Division(val gender: Gender, val level: Level)


fun Division.availableIn(divisions: List<Division>): ValidatedNel<InvalidDivision, Division> {
    val result = divisions.find { it == this }
        ?: return InvalidDivision(nonEmptyListOf("$this is unavailable")).invalidNel()
    return result.validNel()
}

fun Division.accept(athlete: Athlete): ValidatedNel<InvalidDivision, Division> =
    if (this.gender == athlete.gender) validNel() else "$this cannot accept ${athlete.gender}".invalidNel().mapLeft(
        toInvalidField(::InvalidDivision)
    )
