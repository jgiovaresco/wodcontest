package fr.wc.core.model.championship

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.optics.optics
import fr.wc.core.model.Athlete
import fr.wc.core.model.Division
import java.time.LocalDate

enum class ChampionshipStatus {
    Created,
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
}

@optics
data class Championship(
    val id: ChampionshipId,
    val info: ChampionshipInfo,
    val status: ChampionshipStatus,
    val registeredAthletes: RegisteredAthletes,
) {
    companion object {
        fun createdChampionship(id: String, name: String, date: LocalDate, divisions: List<Division>) =
            Championship(
                id = ChampionshipId(id),
                info = ChampionshipInfo(name, date, divisions),
                status = ChampionshipStatus.Created,
                registeredAthletes = RegisteredAthletes()
            )
    }
}
