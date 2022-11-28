package fr.wc.core.usecase

import fr.wc.core.error.AthleteNotFound
import fr.wc.core.error.ChampionshipNotFound
import fr.wc.core.error.EventNotFound
import fr.wc.core.error.IncorrectScoreType
import fr.wc.core.model.*
import fr.wc.core.model.championship.ChampionshipBuilder
import fr.wc.core.model.championship.EventScore
import fr.wc.inmemory.repository.InMemoryChampionshipRepository
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import strikt.api.*
import strikt.arrow.isRight
import strikt.assertions.*

class RegisterScoreTest : ShouldSpec({
    val championshipRepository = InMemoryChampionshipRepository()
    val usecase = RegisterScore(championshipRepository)

    afterTest { championshipRepository.reset() }

    context("when a championship has been created and started") {
        val championship = ChampionshipBuilder.aChampionship().withAthletes().withEvents(
            anEvent(scoreType = ScoreType.Time),
            anEvent(scoreType = ScoreType.Reps),
            anEvent(scoreType = ScoreType.Weight),
        ).started()

        beforeTest {
            championshipRepository.save(championship)
        }

        should("register the event's score of an athlete") {
            val eventId = championship.registeredEvents.first().id
            val athleteId = championship.registeredAthletes.athletesFrom(championship.info.divisions.first()).first().id

            val command = aRegisterScoreCommand(
                championshipId = championship.id,
                eventId = eventId,
                athleteId = athleteId,
                score = aTimeScore()
            )
            val result = usecase.execute(command)

            expectThat(result).isRight().with({ value }) {
                get { scores }.contains(EventScore(command.eventId, command.athleteId, command.score))
            }
        }

        should("prevent from registering a score for an unknown event") {
            val eventId = EventId("unknown")
            val athleteId = championship.registeredAthletes.athletesFrom(championship.info.divisions.first()).first().id

            val command = aRegisterScoreCommand(
                championshipId = championship.id,
                eventId = eventId,
                athleteId = athleteId,
                score = aWeightScore()
            )
            val result = usecase.execute(command)

            result.fold(
                { r -> expectThat(r).isA<EventNotFound>() },
                { fail("error expected") }
            )
        }

        should("prevent from registering an invalid score for an event") {
            val eventId = championship.registeredEvents.first().id
            val athleteId = championship.registeredAthletes.athletesFrom(championship.info.divisions.first()).first().id

            val command = aRegisterScoreCommand(
                championshipId = championship.id,
                eventId = eventId,
                athleteId = athleteId,
                score = aWeightScore()
            )
            val result = usecase.execute(command)

            result.fold(
                { r -> expectThat(r).isA<IncorrectScoreType>() },
                { fail("error expected") }
            )
        }

        should("prevent from registering a score for an unknown athlete") {
            val eventId = championship.registeredEvents.first().id
            val athleteId = AthleteId("unknown")

            val command = aRegisterScoreCommand(
                championshipId = championship.id,
                eventId = eventId,
                athleteId = athleteId,
                score = aTimeScore()
            )
            val result = usecase.execute(command)

            result.fold(
                { r -> expectThat(r).isA<AthleteNotFound>() },
                { fail("error expected") }
            )
        }
    }

    context("when the championship does not exist") {
        should("fail with a NotFoundException") {
            val command = aRegisterScoreCommand()

            val result = usecase.execute(command)

            result.fold(
                { r -> expectThat(r).isA<ChampionshipNotFound>() },
                { fail("error expected") }
            )
        }
    }
})
