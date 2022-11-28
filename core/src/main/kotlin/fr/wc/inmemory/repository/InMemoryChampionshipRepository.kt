package fr.wc.inmemory.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import fr.wc.core.error.AlreadyExistingChampionship
import fr.wc.core.error.ApplicationError
import fr.wc.core.error.ChampionshipNotFound
import fr.wc.core.model.championship.Championship
import fr.wc.core.model.championship.ChampionshipId
import fr.wc.core.repository.ChampionshipRepository

class InMemoryChampionshipRepository : ChampionshipRepository {
    private val storage = mutableListOf<Championship>()

    override suspend fun save(championship: Championship): Either<ApplicationError, Championship> {
        val result = storage.find { it.info.name == championship.info.name }

        if (result == null || result.id == championship.id) {
            val found = storage.indexOfFirst { it.id == championship.id }

            if (found >= 0) {
                storage[found] = championship
            } else {
                storage.add(championship)
            }

            return Either.Right(championship)
        }

        return Either.Left(AlreadyExistingChampionship(championship.info.name))
    }

    override suspend fun get(championshipId: ChampionshipId): Either<ApplicationError, Championship> {
        val result = storage.find { it.id == championshipId } ?: return ChampionshipNotFound(championshipId).left()

        return result.right()
    }

    fun reset() {
        storage.clear()
    }
}
