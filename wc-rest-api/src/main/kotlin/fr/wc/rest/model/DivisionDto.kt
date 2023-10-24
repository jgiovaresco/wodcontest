package fr.wc.rest.model

import fr.wc.core.model.Division
import fr.wc.core.model.Gender
import fr.wc.core.model.Level
import jakarta.validation.constraints.Pattern

data class DivisionDto(
        @Pattern(regexp = "Male|Female")
        val gender: String,
        @Pattern(regexp = "RX|SCALED")
        val level: String) {

        companion object {
                fun fromModel(model: Division) = DivisionDto(genderName(model), levelName(model))
        }
}

fun DivisionDto.toModel() = Division(Gender.valueOf(this.gender), Level.valueOf(this.level))

fun genderName(division: Division) = division.gender.name
fun levelName(division: Division) = division.level.name
