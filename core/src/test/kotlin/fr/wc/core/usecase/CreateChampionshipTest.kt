package fr.wc.core.usecase

import arrow.core.flatMap
import arrow.core.nonEmptyListOf
import fr.wc.core.InvalidDate
import fr.wc.core.InvalidName
import fr.wc.core.error.AlreadyExistingChampionship
import fr.wc.core.error.IncorrectInput
import fr.wc.core.model.ChampionshipStatus
import fr.wc.core.model.aCreateChampionshipCommand
import fr.wc.inmemory.repository.InMemoryChampionshipRepository
import fr.wc.utils.IdGenerator
import fr.wc.utils.TimeProvider
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import strikt.api.*
import strikt.arrow.isRight
import strikt.assertions.*

class CreateChampionshipTest :
    ShouldSpec({
        val championshipRepository = InMemoryChampionshipRepository()
        val usecase = CreateChampionship(championshipRepository)

        afterTest {
            IdGenerator.reset()
            championshipRepository.reset()
        }

        should("return the created championship") {
            val id = "a-championship-id"
            IdGenerator.overrideGenerateFn { id }

            val command = aCreateChampionshipCommand()
            val result = usecase.execute(command)

            expectThat(result).isRight().with({ value }) {
                get { id }.isEqualTo(id)
                get { status }.isEqualTo(ChampionshipStatus.Created)
                get { info.name }.isEqualTo(command.name)
                get { info.date }.isEqualTo(command.date)
            }
        }

        should("save the created championship") {
            val command = aCreateChampionshipCommand()

            val result = usecase.execute(command)

            result.fold(
                { e -> fail("No error expected but $e") },
                { r ->
                    val found = championshipRepository.get(r.id)
                    expectThat(found).isRight().with({ value }) {
                        get { status }.isEqualTo(ChampionshipStatus.Created)
                        get { info.name }.isEqualTo(command.name)
                        get { info.date }.isEqualTo(command.date)
                    }
                }
            )
        }

        should("not create a championship with an empty name") {
            val command = aCreateChampionshipCommand(name = "")

            val result = usecase.execute(command)

            result.fold(
                { r ->
                    expectThat(r).isA<IncorrectInput>().get { errors }
                        .contains(InvalidName(nonEmptyListOf("Cannot be blank")))
                },
                { fail("error expected") }
            )
        }

        should("return an error when creating a championship scheduled in the past") {
            val dateInPast = TimeProvider.today().minusDays(2)
            val command = aCreateChampionshipCommand(date = dateInPast)

            val result = usecase.execute(command)

            result.fold(
                { r ->
                    expectThat(r).isA<IncorrectInput>().get { errors }
                        .contains(InvalidDate(nonEmptyListOf("Cannot be scheduled in the past")))
                },
                { fail("error expected") }
            )
        }

        should("return an error when creating a championship using an already existing name") {
            val command1 = aCreateChampionshipCommand(name = "a championship")
            val command2 = aCreateChampionshipCommand(name = "a championship")

            usecase
                .execute(command1)
                .flatMap { usecase.execute(command2) }
                .fold(
                    { r -> expectThat(r).isA<AlreadyExistingChampionship>() },
                    { fail("error expected") }
                )
        }
    })
