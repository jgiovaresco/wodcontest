package fr.wc.core.model

import arrow.core.Either
import arrow.core.left
import arrow.core.right
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

fun Event.accept(score: Score): Either<IncorrectScoreType, Score> =
    when {
        this.scoreType != score.type -> IncorrectScoreType.left()
        else -> score.right()
    }
