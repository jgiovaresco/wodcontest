package fr.wc.core.usecase

import arrow.core.*
import fr.wc.core.error.ApplicationError
import fr.wc.core.error.ChampionshipNotFound
import fr.wc.core.model.Event
import fr.wc.core.model.EventId
import fr.wc.core.model.championship.Championship
import fr.wc.core.model.championship.registeredEvents
import fr.wc.core.model.command.RegisterEventCommand
import fr.wc.core.repository.ChampionshipRepository
import fr.wc.utils.IdGenerator

class RegisterEvent(private val championshipRepository: ChampionshipRepository) :
  UseCase<RegisterEventCommand, Championship> {
  override suspend fun execute(
      input: RegisterEventCommand,
  ): Either<ApplicationError, Championship> {
    // TODO handle other type of Championship
      val championship = championshipRepository.get(input.championshipId)

    return when (championship) {
        is Some -> registerEvent(championship.value, input).right()
        is None -> ChampionshipNotFound(input.championshipId).left()
    }.flatMap { championshipRepository.save(it) }
  }


  private fun registerEvent(championship: Championship, command: RegisterEventCommand): Championship {
    val newEvent =
      Event(EventId(IdGenerator.generate()), command.name, command.scoreType, command.description)

    return Championship.registeredEvents.modify(championship) {
      val list = mutableListOf<Event>()
      list.addAll(it)
      list.add(newEvent)
      list
    }
  }
}
