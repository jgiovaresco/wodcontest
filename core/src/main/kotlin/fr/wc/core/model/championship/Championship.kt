package fr.wc.core.model.championship

import fr.wc.core.model.Division
import java.time.LocalDate

enum class ChampionshipStatus {
    Created,
}

data class ChampionshipId(val value: String)

data class ChampionshipInfo(val name: String, val date: LocalDate, val divisions: List<Division>)

data class Championship(
    val id: ChampionshipId,
    val info: ChampionshipInfo,
    val status: ChampionshipStatus,
) {
    companion object {
        fun createdChampionship(id: String, name: String, date: LocalDate, divisions: List<Division>) =
            Championship(
                id = ChampionshipId(id),
                info = ChampionshipInfo(name, date, divisions),
                status = ChampionshipStatus.Created
            )
    }
}
