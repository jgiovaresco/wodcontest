package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import fr.wc.core.error.ApplicationError
import fr.wc.core.error.AthleteNotFound
import fr.wc.core.error.EventNotFound
import fr.wc.core.error.IncorrectScoreType
import fr.wc.core.model.Championship
import fr.wc.core.model.EventScore
import fr.wc.core.model.command.RegisterScoreCommand
import fr.wc.core.model.scores
import fr.wc.core.repository.ChampionshipRepository

class RegisterScore(private val championshipRepository: ChampionshipRepository) :
    UseCase<RegisterScoreCommand, Championship> {
    override suspend fun execute(input: RegisterScoreCommand): Either<ApplicationError, Championship> = either {
        // TODO handle other type of Championship
        val championship = championshipRepository.get(input.championshipId).bind()
        val updated = registerScore(championship, input).bind()
        championshipRepository.save(updated).bind()
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
