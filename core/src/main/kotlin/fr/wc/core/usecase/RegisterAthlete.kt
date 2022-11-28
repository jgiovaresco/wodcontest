package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.Validated
import arrow.core.continuations.either
import arrow.core.zip
import fr.wc.core.error.ApplicationError
import fr.wc.core.error.IncorrectInput
import fr.wc.core.model.*
import fr.wc.core.model.command.RegisterAthleteCommand
import fr.wc.core.repository.ChampionshipRepository

class RegisterAthlete(private val championshipRepository: ChampionshipRepository) :
    UseCase<RegisterAthleteCommand, Championship> {
    override suspend fun execute(
        input: RegisterAthleteCommand,
    ): Either<ApplicationError, Championship> = either {
        // TODO handle other type of Championship
        val championship = championshipRepository.get(input.championshipId).bind()
        val command = input.validate(championship).bind()
        val updated = championship.registerAthlete(command.division, command.athlete)
        championshipRepository.save(updated).bind()
    }
}

fun RegisterAthleteCommand.validate(championship: Championship): Validated<IncorrectInput, RegisterAthleteCommand> =
    division.availableIn(championship.info.divisions)
        .zip(division.accept(this.athlete)) { _, _ -> RegisterAthleteCommand(championshipId, athlete, division) }
        .mapLeft(::IncorrectInput)
