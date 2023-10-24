package fr.wc.rest.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class CreateChampionshipRequest(
        @field:NotBlank(message = "Name is required")
        var name: String?,
        @field:NotBlank(message = "Date is required")
        var date: String?,
        @field:NotNull(message = "Divisions is required")
        var divisions: List<DivisionDto>?)
