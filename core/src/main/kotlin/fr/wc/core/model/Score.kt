package fr.wc.core.model

import java.util.*

enum class ScoreType(val unit: ScoreUnit, val order: ScoreOrdering) {
    Time(ScoreUnit.Seconds, ScoreOrdering.Asc),
    Weight(ScoreUnit.Kg, ScoreOrdering.Desc),
    Reps(ScoreUnit.Reps, ScoreOrdering.Desc)
}

enum class ScoreUnit {
    Seconds,
    Kg,
    Reps
}

enum class ScoreOrdering {
    Asc,
    Desc
}

sealed class Score(val type: ScoreType, val value: UInt) : Comparable<Score> {
    override operator fun compareTo(other: Score): Int =
        if (type.order == ScoreOrdering.Desc) {
            value.compareTo(other.value)
        } else {
            -value.compareTo(other.value)
        }

    override fun equals(other: Any?): Boolean =
        other is Score && type == other.type && value == other.value

    override fun hashCode(): Int = Objects.hash(type, value)
}

class TimeScore(valueInSeconds: UInt) : Score(ScoreType.Time, valueInSeconds)

class WeightScore(valueInKg: UInt) : Score(ScoreType.Weight, valueInKg)

class RepScore(repNumber: UInt) : Score(ScoreType.Reps, repNumber)
