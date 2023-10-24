package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.raise.either
import fr.wc.core.InvalidName
import fr.wc.core.error.ApplicationError
import fr.wc.core.error.IncorrectInput
import fr.wc.core.model.Championship
import fr.wc.core.model.Event
import fr.wc.core.model.EventId
import fr.wc.core.model.command.RegisterEventCommand
import fr.wc.core.model.registerNewEvent
import fr.wc.core.notBlank
import fr.wc.core.repository.ChampionshipRepository
import fr.wc.core.toInvalidField
import fr.wc.utils.IdGenerator

class RegisterEvent(private val championshipRepository: ChampionshipRepository) :
    UseCase<RegisterEventCommand, Championship> {
    override suspend fun execute(input: RegisterEventCommand): Either<ApplicationError, Championship> = either {
        // TODO handle other type of Championship
        val championship = championshipRepository.get(input.championshipId).bind()
        val newEvent = input.validate().bind()
        val updated = championship.registerNewEvent(newEvent)
        championshipRepository.save(updated).bind()
    }
}

fun String.validEventName(): EitherNel<InvalidName, String> =
    trim().notBlank().mapLeft(toInvalidField(::InvalidName))

fun RegisterEventCommand.validate(): Either<IncorrectInput, Event> =
    name.validEventName()
        .mapLeft(::IncorrectInput)
        .map { Event(EventId(IdGenerator.generate()), this.name, this.scoreType, this.description) }
