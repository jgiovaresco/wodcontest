package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import fr.wc.core.InvalidDivision
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

fun RegisterAthleteCommand.validate(championship: Championship): Either<IncorrectInput, RegisterAthleteCommand> =
    either<NonEmptyList<InvalidDivision>, RegisterAthleteCommand> {
        division.availableIn(championship.info.divisions).bind()
        division.accept(athlete).bind<Division>()
        RegisterAthleteCommand(championshipId, athlete, division)
    }
        .mapLeft(::IncorrectInput)
