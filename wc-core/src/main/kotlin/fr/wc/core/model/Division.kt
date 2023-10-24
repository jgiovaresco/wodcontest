package fr.wc.core.model

import arrow.core.EitherNel
import arrow.core.leftNel
import arrow.core.nonEmptyListOf
import arrow.core.right
import fr.wc.core.InvalidDivision
import fr.wc.core.toInvalidField

data class Division(val gender: Gender, val level: Level)


fun Division.availableIn(divisions: List<Division>): EitherNel<InvalidDivision, Division> {
    val result = divisions.find { it == this }
            ?: return InvalidDivision(nonEmptyListOf("$this is unavailable")).leftNel()
    return result.right()
}

fun Division.accept(athlete: Athlete): EitherNel<InvalidDivision, Division> =
        if (this.gender == athlete.gender) right() else "$this cannot accept ${athlete.gender}".leftNel().mapLeft(
        toInvalidField(::InvalidDivision)
    )
