package fr.wc.core.usecase

import arrow.core.continuations.either
import fr.wc.core.error.ApplicationError
import fr.wc.core.model.*
import fr.wc.core.model.championship.ChampionshipBuilder
import fr.wc.core.model.leaderboard.OverallRankTuple
import fr.wc.core.model.leaderboard.RankTuple
import fr.wc.core.model.leaderboard.toEventLeaderboardPair
import fr.wc.core.model.leaderboard.toOverallRankTuple
import fr.wc.inmemory.repository.InMemoryChampionshipRepository
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import strikt.api.*
import strikt.arrow.isRight
import strikt.assertions.*

class GetOverallLeaderboardTest : ShouldSpec({
    val championshipRepository = InMemoryChampionshipRepository()
    val usecase = GetOverallLeaderboard(championshipRepository)

    val athletes = listOf(
        aMaleAthlete(id = AthleteId("athlete0")),
        aMaleAthlete(id = AthleteId("athlete1")),
        aMaleAthlete(id = AthleteId("athlete2")),
        aMaleAthlete(id = AthleteId("athlete3")),
    )
    val events = listOf(
        anEvent(id = EventId("event0"), scoreType = ScoreType.Time),
        anEvent(id = EventId("event1"), scoreType = ScoreType.Reps),
        anEvent(id = EventId("event2"), scoreType = ScoreType.Weight),
    )
    val championshipBuilder = ChampionshipBuilder.aChampionship(divisions = listOf(maleRXDivision()))
        .withAthletes(athletes.map { Pair(maleRXDivision(), it) })
        .withEvents(events)

    afterTest {
        championshipBuilder.withScores(listOf())
        championshipRepository.reset()
    }

    context("when a championship has been created and started") {
        should("return the leaderboard of all events") {
            val result = either {
                val championship = championshipRepository.save(
                    championshipBuilder
                        .withScores(
                            anEventScore(events[0].id, athletes[0].id, aTimeScore(305u)),
                            anEventScore(events[0].id, athletes[1].id, aTimeScore(302u)),
                            anEventScore(events[0].id, athletes[2].id, aTimeScore(299u)),
                            anEventScore(events[0].id, athletes[3].id, aTimeScore(320u)),
                            anEventScore(events[1].id, athletes[0].id, aWeightScore(105u)),
                            anEventScore(events[1].id, athletes[1].id, aWeightScore(92u)),
                            anEventScore(events[1].id, athletes[2].id, aWeightScore(90u)),
                            anEventScore(events[1].id, athletes[3].id, aWeightScore(95u)),
                            anEventScore(events[2].id, athletes[0].id, aRepScore(55u)),
                            anEventScore(events[2].id, athletes[1].id, aRepScore(54u)),
                            anEventScore(events[2].id, athletes[2].id, aRepScore(45u)),
                            anEventScore(events[2].id, athletes[3].id, aRepScore(40u)),
                        )
                        .started()
                ).bind()

                usecase.execute(
                    aGetOverallLeaderboardQuery(
                        championshipId = championship.id,
                    )
                ).bind()
            }

            expectThat(result).isRight().with({ value }) {
                get { eventsRanking }.map { it.toEventLeaderboardPair() }.containsExactlyInAnyOrder(
                    Pair(
                        events[0].id, listOf(
                            RankTuple(athletes[2].id, TimeScore(299u), 1u, false, 100u),
                            RankTuple(athletes[1].id, TimeScore(302u), 2u, false, 95u),
                            RankTuple(athletes[0].id, TimeScore(305u), 3u, false, 90u),
                            RankTuple(athletes[3].id, TimeScore(320u), 4u, false, 85u),
                        )
                    ),
                    Pair(
                        events[1].id, listOf(
                            RankTuple(athletes[0].id, WeightScore(105u), 1u, false, 100u),
                            RankTuple(athletes[3].id, WeightScore(95u), 2u, false, 95u),
                            RankTuple(athletes[1].id, WeightScore(92u), 3u, false, 90u),
                            RankTuple(athletes[2].id, WeightScore(90u), 4u, false, 85u),
                        )
                    ),
                    Pair(
                        events[2].id, listOf(
                            RankTuple(athletes[0].id, RepScore(55u), 1u, false, 100u),
                            RankTuple(athletes[1].id, RepScore(54u), 2u, false, 95u),
                            RankTuple(athletes[2].id, RepScore(45u), 3u, false, 90u),
                            RankTuple(athletes[3].id, RepScore(40u), 4u, false, 85u),
                        )
                    ),
                )
            }
        }

        should("return the overall ranking") {
            val result = either {
                val championship = championshipRepository.save(
                    championshipBuilder
                        .withScores(
                            anEventScore(events[0].id, athletes[0].id, aTimeScore(305u)),
                            anEventScore(events[0].id, athletes[1].id, aTimeScore(302u)),
                            anEventScore(events[0].id, athletes[2].id, aTimeScore(299u)),
                            anEventScore(events[0].id, athletes[3].id, aTimeScore(320u)),
                            anEventScore(events[1].id, athletes[0].id, aWeightScore(105u)),
                            anEventScore(events[1].id, athletes[1].id, aWeightScore(92u)),
                            anEventScore(events[1].id, athletes[2].id, aWeightScore(90u)),
                            anEventScore(events[1].id, athletes[3].id, aWeightScore(95u)),
                            anEventScore(events[2].id, athletes[0].id, aRepScore(55u)),
                            anEventScore(events[2].id, athletes[1].id, aRepScore(54u)),
                            anEventScore(events[2].id, athletes[2].id, aRepScore(45u)),
                            anEventScore(events[2].id, athletes[3].id, aRepScore(40u)),
                        )
                        .started()
                ).bind()

                usecase.execute(
                    aGetOverallLeaderboardQuery(
                        championshipId = championship.id,
                    )
                ).bind()
            }

            expectThat(result).isRight().with({ value }) {
                get { ranking }.map { it.toOverallRankTuple() }.isEqualTo(
                    listOf(
                        OverallRankTuple(athletes[0].id, 1u, 290u, false),
                        OverallRankTuple(athletes[1].id, 2u, 280u, false),
                        OverallRankTuple(athletes[2].id, 3u, 275u, false),
                        OverallRankTuple(athletes[3].id, 4u, 265u, false),
                    )
                )
            }
        }

        should("return the overall ranking with tie score") {
            val result = either {
                val championship = championshipRepository.save(
                    championshipBuilder
                        .withScores(
                            anEventScore(events[0].id, athletes[0].id, aTimeScore(305u)),
                            anEventScore(events[0].id, athletes[1].id, aTimeScore(302u)),
                            anEventScore(events[0].id, athletes[2].id, aTimeScore(299u)),
                            anEventScore(events[0].id, athletes[3].id, aTimeScore(320u)),
                            anEventScore(events[1].id, athletes[0].id, aWeightScore(105u)),
                            anEventScore(events[1].id, athletes[1].id, aWeightScore(92u)),
                            anEventScore(events[1].id, athletes[2].id, aWeightScore(90u)),
                            anEventScore(events[1].id, athletes[3].id, aWeightScore(95u)),
                            anEventScore(events[2].id, athletes[0].id, aRepScore(55u)),
                            anEventScore(events[2].id, athletes[1].id, aRepScore(54u)),
                            anEventScore(events[2].id, athletes[2].id, aRepScore(40u)),
                            anEventScore(events[2].id, athletes[3].id, aRepScore(45u)),
                        )
                        .started()
                ).bind()

                usecase.execute(
                    aGetOverallLeaderboardQuery(
                        championshipId = championship.id,
                    )
                ).bind()
            }

            expectThat(result).isRight().with({ value }) {
                get { ranking }.map { it.toOverallRankTuple() }.isEqualTo(
                    listOf(
                        OverallRankTuple(athletes[0].id, 1u, 290u, false),
                        OverallRankTuple(athletes[1].id, 2u, 280u, false),
                        OverallRankTuple(athletes[2].id, 3u, 270u, true),
                        OverallRankTuple(athletes[3].id, 3u, 270u, true),
                    )
                )
            }
        }
    }

    context("when the championship does not exist") {
        should("fail with a NotFoundException") {
            val query = aGetOverallLeaderboardQuery()

            val result = usecase.execute(query)

            result.fold(
                { r -> expectThat(r).isA<ApplicationError.ChampionshipNotFound>() },
                { fail("error expected") }
            )
        }
    }
})
