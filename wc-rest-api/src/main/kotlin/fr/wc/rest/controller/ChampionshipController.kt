package fr.wc.rest.controller

import fr.wc.core.model.ChampionshipId
import fr.wc.core.model.command.CreateChampionshipCommand
import fr.wc.core.repository.ChampionshipRepository
import fr.wc.core.usecase.CreateChampionship
import fr.wc.rest.adapter.ApplicationErrorResponse
import fr.wc.rest.model.CreateChampionshipRequest
import fr.wc.rest.model.toModel
import fr.wc.rest.resource.toResource
import jakarta.validation.Valid
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate


@RestController
@RequestMapping("/championships")
class ChampionshipController(val createChampionship: CreateChampionship, val championshipRepository: ChampionshipRepository) {

    @PostMapping()
    suspend fun createChampionship(@RequestBody @Valid request: CreateChampionshipRequest): Any =
            createChampionship.execute(CreateChampionshipCommand(
                    name = request.name!!,
                    date = LocalDate.parse(request.date),
                    divisions = request.divisions!!.map { it.toModel() }
            )).fold(
                    { error -> ApplicationErrorResponse(error) },
                    { championship ->
                        val headers = HttpHeaders().apply {
                            location = linkTo<ChampionshipController> { getChampionship(championship.id.value) }.withSelfRel().toUri()
                        }
                        ResponseEntity<Unit>(headers, HttpStatus.CREATED)
                    }
            )

    @GetMapping(path = ["/{id}"])
    suspend fun getChampionship(@PathVariable("id") id: String): Any =
            championshipRepository.get(ChampionshipId(id)).fold(
                    { error -> ApplicationErrorResponse(error) },
                    { championship -> championship.toResource() })
}
