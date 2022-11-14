package fr.wc.core.model

import arrow.optics.optics

@optics
data class EventId(val id: String) {
    companion object
}

@optics
data class Event(
    val id: EventId,
    val name: String,
    val scoreType: ScoreType,
    val description: String?,
) {
    companion object
}
