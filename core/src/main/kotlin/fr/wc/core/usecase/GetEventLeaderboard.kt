package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.right
import fr.wc.core.error.ApplicationError
import fr.wc.core.model.championship.Championship
import fr.wc.core.model.leaderboard.EventLeaderboard
import fr.wc.core.model.leaderboard.rankAthletes
import fr.wc.core.model.query.GetEventLeaderboardQuery
import fr.wc.core.repository.ChampionshipRepository

class GetEventLeaderboard(private val championshipRepository: ChampionshipRepository) :
    UseCase<GetEventLeaderboardQuery, EventLeaderboard> {
    override suspend fun execute(input: GetEventLeaderboardQuery): Either<ApplicationError, EventLeaderboard> = either {
        // TODO handle other type of Championship
        val championship = championshipRepository.get(input.championshipId).bind()
        eventLeaderboard(championship, input).bind()
    }

    private fun eventLeaderboard(
        championship: Championship,
        query: GetEventLeaderboardQuery
    ): Either<ApplicationError, EventLeaderboard> = EventLeaderboard(
        eventId = query.eventId, ranking = rankAthletes(championship.scores.filter { it.eventId == query.eventId })
    ).right()
}
