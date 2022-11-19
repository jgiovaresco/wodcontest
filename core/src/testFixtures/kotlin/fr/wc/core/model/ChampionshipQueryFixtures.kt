package fr.wc.core.model

import fr.wc.core.model.championship.ChampionshipId
import fr.wc.core.model.query.GetEventLeaderboardQuery

fun aGetEventLeaderboardQuery(
    championshipId: ChampionshipId = ChampionshipId(faker.random.nextUUID()),
    eventId: EventId = EventId(faker.random.nextUUID()),
) = GetEventLeaderboardQuery(championshipId, eventId)
