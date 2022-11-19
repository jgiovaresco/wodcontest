package fr.wc.core.model.leaderboard

import arrow.core.Tuple5
import arrow.optics.Iso
import arrow.optics.optics
import fr.wc.core.model.*
import fr.wc.core.model.championship.EventScore
import fr.wc.utils.last


typealias RankTuple = Tuple5<AthleteId, Score, UInt, Boolean, UInt>
typealias EventLeaderboardPair = Pair<EventId, List<RankTuple>>

@optics
data class Rank(val athleteId: AthleteId, val score: Score, val position: UInt, val tie: Boolean = false) {
    private val maxScore = 100u
    private val rankScoreDiff = 5u

    val points: UInt = maxScore - (rankScoreDiff * (position - 1u))

    companion object {
        val tupleIso: Iso<Rank, RankTuple> = Iso(
            get = { Tuple5(it.athleteId, it.score, it.position, it.tie, it.points) },
            reverseGet = { Rank(it.first, it.second, it.third, it.fourth) }
        )
    }
}

fun Rank.toRankTuple() = Rank.tupleIso.get(this)
fun RankTuple.toRank() = Rank.tupleIso.reverseGet(this)

@optics
data class EventLeaderboard(val eventId: EventId, val ranking: List<Rank>) {
    companion object {
        val pairIso: Iso<EventLeaderboard, EventLeaderboardPair> = Iso(
            get = { Pair(it.eventId, it.ranking.map { r -> r.toRankTuple() }) },
            reverseGet = { EventLeaderboard(it.first, it.second.map { r -> r.toRank() }) }
        )
    }
}

fun EventLeaderboard.toEventLeaderboardPair() = EventLeaderboard.pairIso.get(this)

fun rankAthletes(
    scores: List<EventScore>,
): List<Rank> {
    val sorted = scores.sortedBy { it.score }

    val ranks = sorted.fold(mutableListOf()) { ranks: MutableList<Rank>, score: EventScore ->
        var lastRank = last(ranks) ?: return@fold mutableListOf(Rank(score.athleteId, score.score, 1u))
        var currentRank = Rank(
            score.athleteId,
            score.score,
            lastRank.position + 1u
        )

        if (lastRank.score == score.score) {
            lastRank = Rank(
                lastRank.athleteId,
                lastRank.score,
                lastRank.position,
                true
            )
            currentRank = Rank(
                score.athleteId,
                score.score,
                lastRank.position,
                true
            )
        }

        return@fold mutableListOf(* ranks.slice(0 until ranks.size - 1).toTypedArray(), lastRank, currentRank)
    }
    return ranks
}
