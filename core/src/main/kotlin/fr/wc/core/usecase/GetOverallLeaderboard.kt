package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import arrow.core.right
import fr.wc.core.error.ApplicationError
import fr.wc.core.model.championship.Championship
import fr.wc.core.model.leaderboard.*
import fr.wc.core.model.query.GetOverallLeaderboardQuery
import fr.wc.inmemory.repository.InMemoryChampionshipRepository
import fr.wc.utils.last

class GetOverallLeaderboard(private val championshipRepository: InMemoryChampionshipRepository) :
    UseCase<GetOverallLeaderboardQuery, OverallLeaderboard> {
    override suspend fun execute(input: GetOverallLeaderboardQuery): Either<ApplicationError, OverallLeaderboard> {
        return when (val championship = championshipRepository.get(input.championshipId)) {
            is Some -> overallLeaderboard(championship.value)
            is None -> Either.Left(ApplicationError.ChampionshipNotFound(input.championshipId))
        }
    }

    private fun overallLeaderboard(
        championship: Championship,
    ): Either<ApplicationError, OverallLeaderboard> {

        val eventsRanking = championship.scores
            .groupBy { it.eventId }
            .entries.map { EventLeaderboard(it.key, rankAthletes(it.value)) }

        val ranking = eventsRanking.flatMap { it.ranking }.groupBy { it.athleteId }
            .mapValues { it.value.sumOf { rank -> rank.points } }
            .map { Pair(it.key, it.value) }
            .sortedByDescending { it.second }
            .fold(mutableListOf<OverallRank>()) { ranks, pair ->
                var lastRank = last(ranks) ?: return@fold mutableListOf(OverallRank(pair.first, 1u, pair.second))
                var currentRank = OverallRank(
                    pair.first,
                    lastRank.position + 1u,
                    pair.second,
                )

                if (lastRank.points == pair.second) {
                    lastRank = OverallRank.tie.modify(lastRank) { true }
                    currentRank = OverallRank.athleteId.modify(lastRank) { pair.first }
                }

                return@fold mutableListOf(* ranks.slice(0 until ranks.size - 1).toTypedArray(), lastRank, currentRank)
            }

        return OverallLeaderboard(
            ranking,
            eventsRanking
        ).right()
    }
}
