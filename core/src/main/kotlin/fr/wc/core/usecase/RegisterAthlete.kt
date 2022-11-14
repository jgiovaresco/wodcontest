package fr.wc.core.usecase

import arrow.core.*
import fr.wc.core.error.ApplicationError
import fr.wc.core.model.Athlete
import fr.wc.core.model.Division
import fr.wc.core.model.championship.Championship
import fr.wc.core.model.championship.registeredAthletes
import fr.wc.core.model.championship.registrations
import fr.wc.core.model.command.RegisterAthleteCommand
import fr.wc.core.repository.ChampionshipRepository

class RegisterAthlete(private val championshipRepository: ChampionshipRepository) :
    UseCase<RegisterAthleteCommand, Championship> {
    override suspend fun execute(
        command: RegisterAthleteCommand,
    ): Either<ApplicationError, Championship> {
        // TODO handle other type of Championship
        val championship = championshipRepository.get(command.championshipId)

        return when (championship) {
            is Some -> registerAthlete(championship.value, command)
            is None -> Either.Left(ApplicationError.ChampionshipNotFound(command.championshipId))
        }.flatMap { championshipRepository.save(it) }
    }

    private fun registerAthlete(
        championship: Championship,
        command: RegisterAthleteCommand,
    ): Either<ApplicationError, Championship> {

        return championship.info.division(command.division)
            .toEither { ApplicationError.UnavailableDivision(command.division) }
            .map {
                if (!it.accept(command.athlete)) {
                    return ApplicationError.IncorrectDivision(
                        command.division,
                        command.athlete
                    ).left()
                }
                it
            }
            .map { addAthlete(championship, command) }
    }

    private fun addAthlete(championship: Championship, command: RegisterAthleteCommand): Championship {
        return Championship.registeredAthletes.registrations.modify(championship) { l ->
            val list = mutableListOf<Pair<Division, Athlete>>()
            list.addAll(l)
            list.add(Pair(command.division, command.athlete))
            list
        }
    }
}
