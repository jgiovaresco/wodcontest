package fr.wc.core.model

fun anEvent(
    id: EventId = EventId(faker.random.nextUUID()),
    name: String = faker.crossfit.maleAthletes(),
    scoreType: ScoreType = faker.random.nextEnum()
): Event {
    return Event(id, name, scoreType, "Description of $name")
}
