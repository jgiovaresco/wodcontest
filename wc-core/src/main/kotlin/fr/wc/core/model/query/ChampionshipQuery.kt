package fr.wc.core.model.query

import fr.wc.core.model.ChampionshipId
import fr.wc.core.model.EventId

sealed class ChampionshipQuery(open val championshipId: ChampionshipId) : Query

data class GetEventLeaderboardQuery(
    override val championshipId: ChampionshipId,
    val eventId: EventId
) : ChampionshipQuery(championshipId)

data class GetOverallLeaderboardQuery(
    override val championshipId: ChampionshipId,
) : ChampionshipQuery(championshipId)
