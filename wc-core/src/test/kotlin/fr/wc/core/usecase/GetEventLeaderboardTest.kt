package fr.wc.core.usecase

import arrow.core.Tuple4
import arrow.core.continuations.either
import fr.wc.core.error.ChampionshipNotFound
import fr.wc.core.model.*
import fr.wc.core.model.championship.ChampionshipBuilder
import fr.wc.inmemory.repository.InMemoryChampionshipRepository
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import strikt.api.expectThat
import strikt.arrow.isRight
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.map

class GetEventLeaderboardTest : ShouldSpec({
    val championshipRepository = InMemoryChampionshipRepository()
    val usecase = GetEventLeaderboard(championshipRepository)

    val athletes = listOf(
        aMaleAthlete(id = AthleteId("athlete0")),
        aMaleAthlete(id = AthleteId("athlete1")),
        aMaleAthlete(id = AthleteId("athlete2")),
        aMaleAthlete(id = AthleteId("athlete3")),
    )
    val events = listOf(
        anEvent(scoreType = ScoreType.Time),
        anEvent(scoreType = ScoreType.Reps),
        anEvent(scoreType = ScoreType.Weight),
    )
    val championshipBuilder = ChampionshipBuilder.aChampionship(divisions = listOf(maleRXDivision()))
        .withAthletes(athletes.map { Pair(maleRXDivision(), it) })
        .withEvents(events)

    afterTest {
        championshipBuilder.withScores(listOf())
        championshipRepository.reset()
    }

    context("when a championship has been created and started") {
        should("return the leaderboard of the event") {
            val result = either {
                val championship = championshipRepository.save(
                    championshipBuilder
                        .withScores(
                            anEventScore(events[0].id, athletes[0].id, aTimeScore(105u)),
                            anEventScore(events[0].id, athletes[1].id, aTimeScore(95u)),
                            anEventScore(events[0].id, athletes[2].id, aTimeScore(90u)),
                        )
                        .started()
                ).bind()

                usecase.execute(
                    aGetEventLeaderboardQuery(
                        championshipId = championship.id,
                        eventId = events[0].id,
                    )
                ).bind()
            }

            expectThat(result).isRight().with({ value }) {
                get { eventId }.isEqualTo(events[0].id)
                get { ranking }.map { Tuple4(it.athleteId, it.position, it.points, it.tie) }
                    .isEqualTo(
                        listOf(
                            Tuple4(athletes[2].id, 1u, 100u, false),
                            Tuple4(athletes[1].id, 2u, 95u, false),
                            Tuple4(athletes[0].id, 3u, 90u, false),
                        )
                    )
            }
        }

        should("return the leaderboard of the event containing tied score") {
            val result = either {
                val championship = championshipRepository.save(
                    championshipBuilder
                        .withScores(
                            anEventScore(events[0].id, athletes[0].id, aTimeScore(105u)),
                            anEventScore(events[0].id, athletes[1].id, aTimeScore(90u)),
                            anEventScore(events[0].id, athletes[2].id, aTimeScore(95u)),
                            anEventScore(events[0].id, athletes[3].id, aTimeScore(90u)),
                        )
                        .started()
                ).bind()

                usecase.execute(
                    aGetEventLeaderboardQuery(
                        championshipId = championship.id,
                        eventId = events[0].id,
                    )
                ).bind()
            }

            expectThat(result).isRight().with({ value }) {
                get { eventId }.isEqualTo(events[0].id)
                get { ranking }.map { Tuple4(it.athleteId, it.position, it.points, it.tie) }
                    .isEqualTo(
                        listOf(
                            Tuple4(athletes[1].id, 1u, 100u, true),
                            Tuple4(athletes[3].id, 1u, 100u, true),
                            Tuple4(athletes[2].id, 2u, 95u, false),
                            Tuple4(athletes[0].id, 3u, 90u, false),
                        )
                    )
            }
        }
    }

    context("when the championship does not exist") {
        should("fail with a NotFoundException") {
            val query = aGetEventLeaderboardQuery()

            val result = usecase.execute(query)

            result.fold(
                { r -> expectThat(r).isA<ChampionshipNotFound>() },
                { fail("error expected") }
            )
        }
    }
})
