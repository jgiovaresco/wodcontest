package fr.wc.core.model.championship

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
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
    fun division(division: Division): Option<Division> {
        val result = divisions.find { it == division } ?: return None
        return Some(result)
    }
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
