package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.raise.either
import fr.wc.core.error.ApplicationError
import fr.wc.core.model.OverallLeaderboard
import fr.wc.core.model.overallLeaderboard
import fr.wc.core.model.query.GetOverallLeaderboardQuery
import fr.wc.core.repository.ChampionshipRepository

class GetOverallLeaderboard(private val championshipRepository: ChampionshipRepository) :
    UseCase<GetOverallLeaderboardQuery, OverallLeaderboard> {
    override suspend fun execute(input: GetOverallLeaderboardQuery): Either<ApplicationError, OverallLeaderboard> =
        either {
            val championship = championshipRepository.get(input.championshipId).bind()
            championship.overallLeaderboard()
        }
}
