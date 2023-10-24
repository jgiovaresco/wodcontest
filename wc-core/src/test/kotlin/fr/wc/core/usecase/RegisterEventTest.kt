package fr.wc.core.usecase

import arrow.core.nonEmptyListOf
import fr.wc.core.InvalidName
import fr.wc.core.error.ChampionshipNotFound
import fr.wc.core.error.IncorrectInput
import fr.wc.core.model.Event
import fr.wc.core.model.aRegisterEventCommand
import fr.wc.core.model.championship.ChampionshipBuilder.Builder.aChampionship
import fr.wc.inmemory.repository.InMemoryChampionshipRepository
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import strikt.api.expectThat
import strikt.arrow.isRight
import strikt.assertions.contains
import strikt.assertions.containsExactly
import strikt.assertions.isA
import strikt.assertions.map

class RegisterEventTest :
  ShouldSpec({
    val championshipRepository = InMemoryChampionshipRepository()
    val usecase = RegisterEvent(championshipRepository)

    afterTest { championshipRepository.reset() }

    context("when a championship has been created") {
      val championship = aChampionship().created()

      beforeTest {
        championshipRepository.save(championship)
      }

      should("register the 1st event") {
        val command = aRegisterEventCommand(championshipId = championship.id)
        val result = usecase.execute(command)

        expectThat(result).isRight().with({ value }) {
          get { registeredEvents }.map(Event::name).containsExactly(command.name)
        }
      }

      should("save the updated championship") {
        val command = aRegisterEventCommand(championshipId = championship.id)

        usecase.execute(command)

        val found = championshipRepository.get(championship.id)
        expectThat(found).isRight().with({ value }) {
          get { registeredEvents }.map(Event::name).containsExactly(command.name)
        }
      }

      context("prevent from registering an invalid event") {
        val command = aRegisterEventCommand(championshipId = championship.id, name = "")

        val result = usecase.execute(command)

        result.fold(
          { r ->
            expectThat(r).isA<IncorrectInput>().get { errors }
              .contains(InvalidName(nonEmptyListOf("Cannot be blank")))
          },
          { fail("error expected") }
        )
      }
    }

    context("when the championship does not exist") {
      should("fail with a NotFoundException") {
        val command = aRegisterEventCommand()

        val result = usecase.execute(command)

        result.fold(
          { r -> expectThat(r).isA<ChampionshipNotFound>() },
          { fail("error expected") }
        )
      }
    }
  })
