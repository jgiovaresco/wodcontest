package fr.wc.core.model

import fr.wc.core.model.query.GetEventLeaderboardQuery
import fr.wc.core.model.query.GetOverallLeaderboardQuery

fun aGetEventLeaderboardQuery(
    championshipId: ChampionshipId = ChampionshipId(faker.random.nextUUID()),
    eventId: EventId = EventId(faker.random.nextUUID()),
) = GetEventLeaderboardQuery(championshipId, eventId)

fun aGetOverallLeaderboardQuery(
    championshipId: ChampionshipId = ChampionshipId(faker.random.nextUUID()),
) = GetOverallLeaderboardQuery(championshipId)
