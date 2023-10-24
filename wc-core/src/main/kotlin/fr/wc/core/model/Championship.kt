package fr.wc.core.model

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.right
import arrow.optics.optics
import fr.wc.core.error.*
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

fun Championship.registerScore(eventScore: EventScore) =
    Championship.scores.modify(this) {
        val list = mutableListOf<EventScore>()
        list.addAll(it)
        list.add(eventScore)
        list
    }

fun Championship.readyToStart(): Either<ChampionshipStartError, Championship> =
    when {
        info.divisions.any { (registeredAthletes.athletesFrom(it).size < 2) } -> NotEnoughAthlete.left()
        registeredEvents.isEmpty() -> NoEvent.left()
        else -> this.right()
    }

fun Championship.start(): Either<ChampionshipStartError, Championship> = either {
    val championship = readyToStart().bind()
    Championship.status.set(championship, ChampionshipStatus.Started)
}

fun Championship.findEvent(eventId: EventId): Either<ApplicationError, Event> = either {
    ensureNotNull(registeredEvents.find { it.id == eventId }) { EventNotFound }
}

fun Championship.findAthlete(athleteId: AthleteId): Either<ApplicationError, Athlete> = either {
    val pair = ensureNotNull(registeredAthletes.registrations.find { it.second.id == athleteId }) { AthleteNotFound(athleteId) }
    pair.second
}
