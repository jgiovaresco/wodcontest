package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import arrow.core.right
import fr.wc.core.error.ApplicationError
import fr.wc.core.model.championship.Championship
import fr.wc.core.model.leaderboard.EventLeaderboard
import fr.wc.core.model.leaderboard.rankAthletes
import fr.wc.core.model.query.GetEventLeaderboardQuery
import fr.wc.inmemory.repository.InMemoryChampionshipRepository

class GetEventLeaderboard(private val championshipRepository: InMemoryChampionshipRepository) :
    UseCase<GetEventLeaderboardQuery, EventLeaderboard> {
    override suspend fun execute(input: GetEventLeaderboardQuery): Either<ApplicationError, EventLeaderboard> {
        // TODO handle other type of Championship
        val championship = championshipRepository.get(input.championshipId)

        return when (championship) {
            is Some -> eventLeaderboard(championship.value, input)
            is None -> Either.Left(ApplicationError.ChampionshipNotFound(input.championshipId))
        }
    }

    private fun eventLeaderboard(
        championship: Championship,
        query: GetEventLeaderboardQuery
    ): Either<ApplicationError, EventLeaderboard> = EventLeaderboard(
        eventId = query.eventId, ranking = rankAthletes(championship.scores.filter { it.eventId == query.eventId })
    ).right()
}
