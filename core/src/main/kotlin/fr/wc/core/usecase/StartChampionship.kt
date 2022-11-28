package fr.wc.core.usecase

import arrow.core.*
import fr.wc.core.error.ApplicationError
import fr.wc.core.error.ChampionshipNotFound
import fr.wc.core.error.NoEvent
import fr.wc.core.error.NotEnoughAthlete
import fr.wc.core.model.championship.Championship
import fr.wc.core.model.championship.ChampionshipStatus
import fr.wc.core.model.championship.status
import fr.wc.core.model.command.StartChampionshipCommand
import fr.wc.inmemory.repository.InMemoryChampionshipRepository

class StartChampionship(private val championshipRepository: InMemoryChampionshipRepository) :
    UseCase<StartChampionshipCommand, Championship> {
    override suspend fun execute(
        input: StartChampionshipCommand
    ): Either<ApplicationError, Championship> {
        // TODO handle other type of Championship
        val championship = championshipRepository.get(input.championshipId)

        return when (championship) {
            is Some -> start(championship.value)
            is None -> ChampionshipNotFound(input.championshipId).left()
        }.flatMap { championshipRepository.save(it) }
    }

    private fun start(championship: Championship): Either<ApplicationError, Championship> {
        if (championship.registeredEvents.isEmpty()) {
            return NoEvent.left()
        }

        if (!championship.allDivisionsHaveEnoughAthlete()) {
            return NotEnoughAthlete.left()
        }

        return Championship.status.set(championship, ChampionshipStatus.Started).right()
    }
}
