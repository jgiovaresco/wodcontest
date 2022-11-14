package fr.wc.inmemory.repository

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import fr.wc.core.error.ApplicationError
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

        return Either.Left(ApplicationError.AlreadyExistingChampionship(championship.info.name))
    }

    override suspend fun get(championshipId: ChampionshipId): Option<Championship> {
        val result = storage.find { it.id == championshipId } ?: return None

        return Some(result)
    }

    fun reset() {
        storage.clear()
    }
}
