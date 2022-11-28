// Until https://github.com/arrow-kt/arrow/pull/2850 | https://github.com/arrow-kt/arrow/issues/2803 available
@file:JvmName("ChampionshipJvm")

package fr.wc.core.model

import arrow.optics.optics
import fr.wc.core.model.*
import java.time.LocalDate

enum class ChampionshipStatus {
    Created,
    Started,
}

@optics
data class ChampionshipId(val value: String) {
    companion object
}

@optics
data class ChampionshipInfo(val name: String, val date: LocalDate, val divisions: List<Division>) {
    companion object
}

@optics
data class RegisteredAthletes(val registrations: List<Pair<Division, Athlete>> = listOf()) {
    companion object

    fun athletesFrom(division: Division): List<Athlete> =
        registrations.filter { it.first == division }.map { it.second }

    fun contains(id: AthleteId) = registrations.any { it.second.id == id }
}

@optics
data class EventScore(val eventId: EventId, val athleteId: AthleteId, val score: Score) {
    companion object
}

@optics
data class Championship(
    val id: ChampionshipId,
    val info: ChampionshipInfo,
    val status: ChampionshipStatus,
    val registeredAthletes: RegisteredAthletes,
    val registeredEvents: List<Event>,
    val scores: List<EventScore>,
) {
    companion object {
        fun createdChampionship(id: String, name: String, date: LocalDate, divisions: List<Division>) =
            Championship(
                id = ChampionshipId(id),
                info = ChampionshipInfo(name, date, divisions),
                status = ChampionshipStatus.Created,
                registeredAthletes = RegisteredAthletes(),
                registeredEvents = listOf(),
                scores = listOf(),
            )
    }

    fun allDivisionsHaveEnoughAthlete() =
        info.divisions.all { (registeredAthletes.athletesFrom(it).size >= 2) }
}

fun Championship.registerAthlete(division: Division, athlete: Athlete) =
    Championship.registeredAthletes.registrations.modify(this) { l ->
        val list = mutableListOf<Pair<Division, Athlete>>()
        list.addAll(l)
        list.add(Pair(division, athlete))
        list
    }

fun Championship.registerNewEvent(newEvent: Event) =
    Championship.registeredEvents.modify(this) {
        val list = mutableListOf<Event>()
        list.addAll(it)
        list.add(newEvent)
        list
    }
