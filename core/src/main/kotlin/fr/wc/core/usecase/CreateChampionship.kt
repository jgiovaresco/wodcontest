package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.continuations.either
import arrow.core.zip
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

fun String.validChampionshipName(): ValidatedNel<InvalidName, String> =
    trim().notBlank().mapLeft(toInvalidField(::InvalidName))

fun LocalDate.validChampionshipDate(): ValidatedNel<InvalidDate, LocalDate> =
    notScheduleInPast().mapLeft(toInvalidField(::InvalidDate))

fun CreateChampionshipCommand.validate(): Validated<IncorrectInput, CreateChampionshipCommand> =
    name.validChampionshipName()
        .zip(date.validChampionshipDate()) { name, date -> CreateChampionshipCommand(name, date, this.divisions) }
        .mapLeft(::IncorrectInput)
