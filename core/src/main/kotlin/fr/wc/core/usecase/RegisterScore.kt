package fr.wc.core.usecase

import arrow.core.*
import fr.wc.core.error.*
import fr.wc.core.model.championship.Championship
import fr.wc.core.model.championship.EventScore
import fr.wc.core.model.championship.scores
import fr.wc.core.model.command.RegisterScoreCommand
import fr.wc.core.repository.ChampionshipRepository

class RegisterScore(private val championshipRepository: ChampionshipRepository) :
    UseCase<RegisterScoreCommand, Championship> {
    override suspend fun execute(input: RegisterScoreCommand): Either<ApplicationError, Championship> {
        // TODO handle other type of Championship
        val championship = championshipRepository.get(input.championshipId)

        return when (championship) {
            is Some -> registerScore(championship.value, input)
            is None -> ChampionshipNotFound(input.championshipId).left()
        }.flatMap { championshipRepository.save(it) }
    }

    private fun registerScore(
        championship: Championship,
        command: RegisterScoreCommand
    ): Either<ApplicationError, Championship> {

        return Either.fromNullable(championship.registeredEvents.find { it.id == command.eventId })
            .mapLeft { EventNotFound(command.eventId) }
            .map { event ->
                if (event.scoreType != command.score.type) {
                    return IncorrectScoreType(event, command.score).left()
                }

                if (!championship.registeredAthletes.contains(command.athleteId)) {
                    return AthleteNotFound(command.athleteId).left()
                }
            }
            .map {
                Championship.scores.modify(championship) {
                    val list = mutableListOf<EventScore>()
                    list.addAll(it)
                    list.add(EventScore(command.eventId, command.athleteId, command.score))
                    list
                }
            }
    }
}
