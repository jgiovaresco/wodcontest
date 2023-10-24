package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.raise.either
import fr.wc.core.*
import fr.wc.core.error.ApplicationError
import fr.wc.core.error.IncorrectInput
import fr.wc.core.model.Championship
import fr.wc.core.model.command.CreateChampionshipCommand
import fr.wc.core.repository.ChampionshipRepository
import fr.wc.utils.IdGenerator
import java.time.LocalDate

class CreateChampionship(private val championshipRepository: ChampionshipRepository) :
    UseCase<CreateChampionshipCommand, Championship> {
    override suspend fun execute(
        input: CreateChampionshipCommand,
    ): Either<ApplicationError, Championship> = either {
        val (name, date, divisions) = input.validate().bind()
        val championship = Championship.createdChampionship(
            IdGenerator.generate(),
            name,
            date,
            divisions
        )
        championshipRepository.save(championship).bind()
    }
}

fun String.validChampionshipName(): EitherNel<InvalidName, String> =
    trim().notBlank().mapLeft(toInvalidField(::InvalidName))

fun LocalDate.validChampionshipDate(): EitherNel<InvalidDate, LocalDate> =
    notScheduleInPast().mapLeft(toInvalidField(::InvalidDate))

fun CreateChampionshipCommand.validate(): Either<IncorrectInput, CreateChampionshipCommand> =
        either {
            CreateChampionshipCommand(
                    name.validChampionshipName().bind(),
                    date.validChampionshipDate().bind<LocalDate>(),
                    divisions
            )
        }
        .mapLeft(::IncorrectInput)
