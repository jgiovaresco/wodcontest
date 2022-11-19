package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.flatMap
import fr.wc.core.error.ApplicationError
import fr.wc.core.model.championship.Championship
import fr.wc.core.model.command.CreateChampionshipCommand
import fr.wc.core.repository.ChampionshipRepository
import fr.wc.utils.IdGenerator
import fr.wc.utils.TimeProvider

class CreateChampionship(private val championshipRepository: ChampionshipRepository) :
    UseCase<CreateChampionshipCommand, Championship> {
    override suspend fun execute(
        input: CreateChampionshipCommand,
    ): Either<ApplicationError, Championship> {
        return this.validate(input)
            .map {
                Championship.createdChampionship(
                    IdGenerator.generate(),
                    input.name,
                    input.date,
                    input.divisions
                )
            }
            .flatMap { championshipRepository.save(it) }
    }

    private fun validate(
        command: CreateChampionshipCommand,
    ): Either<ApplicationError, CreateChampionshipCommand> {

        if (command.name.isEmpty()) {
            return Either.Left(ApplicationError.EmptyChampionshipName)
        }

        if (command.date.isBefore(TimeProvider.today())) {
            return Either.Left(ApplicationError.ScheduledInPastChampionship)
        }

        return Either.Right(command)
    }
}
