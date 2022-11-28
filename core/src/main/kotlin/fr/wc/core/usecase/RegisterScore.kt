package fr.wc.core.usecase

import arrow.core.Either
import arrow.core.continuations.either
import fr.wc.core.error.ApplicationError
import fr.wc.core.model.*
import fr.wc.core.model.command.RegisterScoreCommand
import fr.wc.core.repository.ChampionshipRepository

class RegisterScore(private val championshipRepository: ChampionshipRepository) :
    UseCase<RegisterScoreCommand, Championship> {
    override suspend fun execute(input: RegisterScoreCommand): Either<ApplicationError, Championship> = either {
        // TODO handle other type of Championship
        val championship = championshipRepository.get(input.championshipId).bind()
        val score = input.validate(championship).bind()
        val updated = championship.registerScore(score)
        championshipRepository.save(updated).bind()
    }
}

fun RegisterScoreCommand.validate(championship: Championship) = either.eager {
    val event = championship.findEvent(eventId).bind()
    val athlete = championship.findAthlete(athleteId).bind()
    val score = event.accept(score).bind()

    EventScore(event.id, athlete.id, score)
}
