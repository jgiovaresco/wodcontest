package fr.wc.core.usecase

import fr.wc.core.error.ApplicationError
import fr.wc.core.model.aStartChampionshipCommand
import fr.wc.core.model.championship.ChampionshipBuilder.Builder.aChampionship
import fr.wc.core.model.championship.ChampionshipStatus
import fr.wc.inmemory.repository.InMemoryChampionshipRepository
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import strikt.api.*
import strikt.arrow.isRight
import strikt.arrow.isSome
import strikt.assertions.*

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
                expectThat(found).isSome().with({ value }) {
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
                    { r -> expectThat(r).isA<ApplicationError.NotEnoughAthlete>() },
                    { fail("error expected") }
                )
            }

            should("prevent starting a championship without event registered") {
                val championship = aChampionship().withAthletes().created()
                championshipRepository.save(championship)

                val command = aStartChampionshipCommand(championshipId = championship.id)
                val result = usecase.execute(command)

                result.fold(
                    { r -> expectThat(r).isA<ApplicationError.NoEvent>() },
                    { fail("error expected") }
                )
            }
        }

        context("when the championship does not exist") {
            should("fail with a NotFoundException") {
                val command = aStartChampionshipCommand()

                val result = usecase.execute(command)

                result.fold(
                    { r -> expectThat(r).isA<ApplicationError.ChampionshipNotFound>() },
                    { fail("error expected") }
                )
            }
        }
    })
