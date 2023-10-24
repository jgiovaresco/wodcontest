// Until https://github.com/arrow-kt/arrow/pull/2850 | https://github.com/arrow-kt/arrow/issues/2803 available
@file:JvmName("EventJvm")

package fr.wc.core.model

import arrow.core.Validated
import arrow.core.invalid
import arrow.core.valid
import arrow.optics.optics
import fr.wc.core.error.IncorrectScoreType

@optics
data class EventId(val id: String) {
    companion object
}

@optics
data class Event(
    val id: EventId,
    val name: String,
    val scoreType: ScoreType,
    val description: String?,
) {
    companion object
}

fun Event.accept(score: Score): Validated<IncorrectScoreType, Score> =
    when {
        this.scoreType != score.type -> IncorrectScoreType.invalid()
        else -> score.valid()
    }
