package fr.wc.core.usecase

import fr.wc.core.error.ChampionshipNotFound
import fr.wc.core.model.Event
import fr.wc.core.model.aRegisterEventCommand
import fr.wc.core.model.championship.ChampionshipBuilder.Builder.aChampionship
import fr.wc.inmemory.repository.InMemoryChampionshipRepository
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import strikt.api.*
import strikt.arrow.isRight
import strikt.arrow.isSome
import strikt.assertions.*

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
        expectThat(found).isSome().with({ value }) {
          get { registeredEvents }.map(Event::name).containsExactly(command.name)
        }
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
