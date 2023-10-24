package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.raise.either
import fr.wc.core.error.ApplicationError
import fr.wc.core.model.EventLeaderboard
import fr.wc.core.model.eventLeaderboard
import fr.wc.core.model.query.GetEventLeaderboardQuery
import fr.wc.core.repository.ChampionshipRepository

class GetEventLeaderboard(private val championshipRepository: ChampionshipRepository) :
    UseCase<GetEventLeaderboardQuery, EventLeaderboard> {
    override suspend fun execute(input: GetEventLeaderboardQuery): Either<ApplicationError, EventLeaderboard> = either {
        // TODO handle other type of Championship
        val championship = championshipRepository.get(input.championshipId).bind()
        championship.eventLeaderboard(input.eventId)
    }
}
