package fr.wc.core

import arrow.core.*
import fr.wc.utils.TimeProvider
import java.time.LocalDate

sealed interface InvalidField {
    val errors: NonEmptyList<String>
    val field: String
}

data class InvalidName(override val errors: NonEmptyList<String>) : InvalidField {
    override val field: String = "name"
}

data class InvalidDate(override val errors: NonEmptyList<String>) : InvalidField {
    override val field: String = "date"
}

data class InvalidDivision(override val errors: NonEmptyList<String>) : InvalidField {
    override val field: String = "division"
}

fun <A : InvalidField> toInvalidField(transform: (NonEmptyList<String>) -> A): (NonEmptyList<String>) -> NonEmptyList<A> =
    { nel -> nonEmptyListOf(transform(nel)) }

fun String.notBlank(): ValidatedNel<String, String> =
    if (isNotBlank()) validNel() else "Cannot be blank".invalidNel()

fun LocalDate.notScheduleInPast(): ValidatedNel<String, LocalDate> =
    if (isBefore(TimeProvider.today())) "Cannot be scheduled in the past".invalidNel() else validNel()
