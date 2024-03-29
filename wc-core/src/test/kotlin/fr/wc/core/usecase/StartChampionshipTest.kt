package fr.wc.core.usecase

import fr.wc.core.error.ChampionshipNotFound
import fr.wc.core.error.NoEvent
import fr.wc.core.error.NotEnoughAthlete
import fr.wc.core.model.ChampionshipStatus
import fr.wc.core.model.aStartChampionshipCommand
import fr.wc.core.model.championship.ChampionshipBuilder.Builder.aChampionship
import fr.wc.inmemory.repository.InMemoryChampionshipRepository
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import strikt.api.expectThat
import strikt.arrow.isRight
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class StartChampionshipTest :
    ShouldSpec({
        val championshipRepository = InMemoryChampionshipRepository()
        val usecase = StartChampionship(championshipRepository)

        afterTest { championshipRepository.reset() }

        context("when a championship has been created") {
            val championship = aChampionship().withAthletes().withEvents().created()

            beforeTest {
                championshipRepository.save(championship)
            }

            should("start the championship") {
                val command = aStartChampionshipCommand(championshipId = championship.id)
                val result = usecase.execute(command)

                expectThat(result).isRight().with({ value }) {
                    get { status }.isEqualTo(ChampionshipStatus.Started)
                }
            }

            should("save the updated championship") {
                val command = aStartChampionshipCommand(championshipId = championship.id)
                usecase.execute(command)

                val found = championshipRepository.get(championship.id)
                expectThat(found).isRight().with({ value }) {
                    get { status }.isEqualTo(ChampionshipStatus.Started)
                }
            }

        }

        context("when the championship is incorrect") {
            should("prevent starting a championship without athlete registered") {
                val championship = aChampionship().withEvents().created()
                championshipRepository.save(championship)

                val command = aStartChampionshipCommand(championshipId = championship.id)
                val result = usecase.execute(command)

                result.fold(
                    { r -> expectThat(r).isA<NotEnoughAthlete>() },
                    { fail("error expected") }
                )
            }

            should("prevent starting a championship without event registered") {
                val championship = aChampionship().withAthletes().created()
                championshipRepository.save(championship)

                val command = aStartChampionshipCommand(championshipId = championship.id)
                val result = usecase.execute(command)

                result.fold(
                    { r -> expectThat(r).isA<NoEvent>() },
                    { fail("error expected") }
                )
            }
        }

        context("when the championship does not exist") {
            should("fail with a NotFoundException") {
                val command = aStartChampionshipCommand()

                val result = usecase.execute(command)

                result.fold(
                    { r -> expectThat(r).isA<ChampionshipNotFound>() },
                    { fail("error expected") }
                )
            }
        }
    })
