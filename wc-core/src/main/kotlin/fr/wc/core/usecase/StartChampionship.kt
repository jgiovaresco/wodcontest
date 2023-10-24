package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.continuations.either
import fr.wc.core.error.ApplicationError
import fr.wc.core.model.Championship
import fr.wc.core.model.command.StartChampionshipCommand
import fr.wc.core.model.start
import fr.wc.core.repository.ChampionshipRepository

class StartChampionship(private val championshipRepository: ChampionshipRepository) :
    UseCase<StartChampionshipCommand, Championship> {
    override suspend fun execute(input: StartChampionshipCommand): Either<ApplicationError, Championship> = either {
        // TODO handle other type of Championship
        val championship = championshipRepository.get(input.championshipId).bind()
        val started = championship.start().bind()
        championshipRepository.save(started).bind()
    }
}
