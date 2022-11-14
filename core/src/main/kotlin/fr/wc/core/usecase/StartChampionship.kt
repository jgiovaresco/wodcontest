package fr.wc.core.usecase

import arrow.core.*
import fr.wc.core.error.ApplicationError
import fr.wc.core.model.championship.Championship
import fr.wc.core.model.championship.ChampionshipStatus
import fr.wc.core.model.championship.status
import fr.wc.core.model.command.StartChampionshipCommand
import fr.wc.inmemory.repository.InMemoryChampionshipRepository

class StartChampionship(private val championshipRepository: InMemoryChampionshipRepository) :
    UseCase<StartChampionshipCommand, Championship> {
    override suspend fun execute(
        command: StartChampionshipCommand
    ): Either<ApplicationError, Championship> {
        // TODO handle other type of Championship
        val championship = championshipRepository.get(command.championshipId)

        return when (championship) {
            is Some -> start(championship.value)
            is None -> ApplicationError.ChampionshipNotFound(command.championshipId).left()
        }.flatMap { championshipRepository.save(it) }
    }

    private fun start(championship: Championship): Either<ApplicationError, Championship> {
        if (championship.registeredEvents.isEmpty()) {
            return ApplicationError.NoEvent.left()
        }

        if (!championship.allDivisionsHaveEnoughAthlete()) {
            return ApplicationError.NotEnoughAthlete.left()
        }

        return Championship.status.set(championship, ChampionshipStatus.Started).right()
    }
}
