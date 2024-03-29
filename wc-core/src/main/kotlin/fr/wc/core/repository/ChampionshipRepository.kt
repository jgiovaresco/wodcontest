package fr.wc.core.repository

import arrow.core.Either
import fr.wc.core.error.ApplicationError
import fr.wc.core.model.Championship
import fr.wc.core.model.ChampionshipId

interface ChampionshipRepository {
    suspend fun save(championship: Championship): Either<ApplicationError, Championship>
    suspend fun get(championshipId: ChampionshipId): Either<ApplicationError, Championship>
}
