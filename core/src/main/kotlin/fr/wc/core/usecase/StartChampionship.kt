package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import fr.wc.core.error.ApplicationError
import fr.wc.core.error.NoEvent
import fr.wc.core.error.NotEnoughAthlete
import fr.wc.core.model.Championship
import fr.wc.core.model.ChampionshipStatus
import fr.wc.core.model.command.StartChampionshipCommand
import fr.wc.core.model.status
import fr.wc.core.repository.ChampionshipRepository

class StartChampionship(private val championshipRepository: ChampionshipRepository) :
    UseCase<StartChampionshipCommand, Championship> {
    override suspend fun execute(input: StartChampionshipCommand): Either<ApplicationError, Championship> = either {
        // TODO handle other type of Championship
        val championship = championshipRepository.get(input.championshipId).bind()
        val updated = start(championship).bind()
        championshipRepository.save(updated).bind()
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
