package fr.wc.core.model

import arrow.optics.optics

@optics
data class AthleteId(val id: String) {
    companion object
}

@optics
data class Athlete(
    val id: AthleteId,
    val name: String,
    val gender: Gender,
) {
    companion object
}
