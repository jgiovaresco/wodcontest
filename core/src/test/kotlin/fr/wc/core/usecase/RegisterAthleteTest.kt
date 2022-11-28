package fr.wc.core.usecase

import fr.wc.core.error.ChampionshipNotFound
import fr.wc.core.error.IncorrectDivision
import fr.wc.core.error.UnavailableDivision
import fr.wc.core.model.*
import fr.wc.core.model.championship.ChampionshipBuilder.Builder.aChampionship
import fr.wc.inmemory.repository.InMemoryChampionshipRepository
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import strikt.api.*
import strikt.arrow.isRight
import strikt.arrow.isSome
import strikt.assertions.*

class RegisterAthleteTest :
    ShouldSpec({
        val rxMaleDivision = Division(Gender.Male, Level.RX)
        val scaledMaleDivision = Division(Gender.Male, Level.SCALED)
        val rxFemaleDivision = Division(Gender.Female, Level.RX)
        val scaledFemaleDivision = Division(Gender.Female, Level.SCALED)

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
                expectThat(found).isSome().with({ value }) {
                    get { registeredAthletes.athletesFrom(rxMaleDivision).map(Athlete::name) }
                        .containsExactly(command.athlete.name)
                }
            }

            context("prevent from registering an athlete in unsupported division") {
                withData(
                    scaledMaleDivision,
                    scaledFemaleDivision,
                ) { division ->
                    val command =
                        aRegisterAthleteCommand(
                            championshipId = championship.id,
                            athlete = aFemaleAthlete(),
                            division = division,
                        )

                    val result = usecase.execute(command)

                    result.fold(
                        { r -> expectThat(r).isA<UnavailableDivision>() },
                        { fail("error expected") }
                    )
                }
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
                    { r -> expectThat(r).isA<IncorrectDivision>() },
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
