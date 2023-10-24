package fr.wc.rest.resource

import fr.wc.core.model.Championship
import fr.wc.core.model.ChampionshipStatus
import fr.wc.rest.controller.ChampionshipController
import fr.wc.rest.model.DivisionDto
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import java.time.LocalDate

open class ChampionshipResource(
        val id: String,
        val name: String,
        val date: LocalDate,
        val status: ChampionshipStatus,
        val divisions: List<DivisionDto>,
) : RepresentationModel<ChampionshipResource>()

suspend fun Championship.toResource(): ChampionshipResource = ChampionshipResource(
        id = this.id.value,
        name = this.info.name,
        date = this.info.date,
        status = this.status,
        divisions = this.info.divisions.map { DivisionDto.fromModel(it) }
).add(linkTo<ChampionshipController> { getChampionship(this@toResource.id.value) }.withSelfRel())
