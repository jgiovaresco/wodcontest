package fr.wc.core.usecase

import arrow.core.nonEmptyListOf
import fr.wc.core.InvalidDivision
import fr.wc.core.error.ChampionshipNotFound
import fr.wc.core.error.IncorrectInput
import fr.wc.core.model.*
import fr.wc.core.model.championship.ChampionshipBuilder.Builder.aChampionship
import fr.wc.inmemory.repository.InMemoryChampionshipRepository
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import strikt.api.expectThat
import strikt.arrow.isRight
import strikt.assertions.contains
import strikt.assertions.containsExactly
import strikt.assertions.isA

class RegisterAthleteTest :
    ShouldSpec({
        val rxMaleDivision = Division(Gender.Male, Level.RX)
        val scaledMaleDivision = Division(Gender.Male, Level.SCALED)
        val rxFemaleDivision = Division(Gender.Female, Level.RX)

        val championshipRepository = InMemoryChampionshipRepository()
        val usecase = RegisterAthlete(championshipRepository)

        afterTest { championshipRepository.reset() }

        context("when a championship has been created") {
            val championship = aChampionship().created()

            beforeTest {
                championshipRepository.save(championship)
            }

            should("register the 1st athlete in the provided division") {
                val command =
                    aRegisterAthleteCommand(
                        championshipId = championship.id,
                        athlete = aMaleAthlete(),
                        division = rxMaleDivision,
                    )
                val result = usecase.execute(command)

                expectThat(result).isRight().with({ value }) {
                    get { registeredAthletes.athletesFrom(rxMaleDivision).map(Athlete::name) }
                        .containsExactly(command.athlete.name)
                }
            }

            should("save the updated championship") {
                val command =
                    aRegisterAthleteCommand(
                        championshipId = championship.id,
                        athlete = aMaleAthlete(),
                        division = rxMaleDivision,
                    )

                usecase.execute(command)

                val found = championshipRepository.get(championship.id)
                expectThat(found).isRight().with({ value }) {
                    get { registeredAthletes.athletesFrom(rxMaleDivision).map(Athlete::name) }
                        .containsExactly(command.athlete.name)
                }
            }

            context("prevent from registering an athlete in unsupported division") {
                val command =
                    aRegisterAthleteCommand(
                        championshipId = championship.id,
                        athlete = aMaleAthlete(),
                        division = scaledMaleDivision,
                    )

                val result = usecase.execute(command)

                result.fold(
                    { r ->
                        expectThat(r).isA<IncorrectInput>().get { errors }
                            .contains(InvalidDivision(nonEmptyListOf("$scaledMaleDivision is unavailable")))
                    },
                    { fail("error expected") }
                )
            }

            should("prevent from registering an athlete in the wrong division") {
                val command =
                    aRegisterAthleteCommand(
                        championshipId = championship.id,
                        athlete = aMaleAthlete(),
                        division = rxFemaleDivision,
                    )

                val result = usecase.execute(command)

                result.fold(
                    { r ->
                        expectThat(r).isA<IncorrectInput>().get { errors }
                            .contains(InvalidDivision(nonEmptyListOf("$rxFemaleDivision cannot accept Male")))
                    },
                    { fail("error expected") }
                )
            }
        }

        context("when the championship does not exist") {
            should("fail with a NotFoundException") {
                val command =
                    aRegisterAthleteCommand(
                        athlete = aMaleAthlete(),
                        division = rxFemaleDivision,
                    )

                val result = usecase.execute(command)

                result.fold(
                    { r -> expectThat(r).isA<ChampionshipNotFound>() },
                    { fail("error expected") }
                )
            }
        }
    })
