package fr.wc.core.error

sealed class ApplicationError {
    // CreateChampionshipError
    object EmptyChampionshipName : ApplicationError()
    object ScheduledInPastChampionship : ApplicationError()
    data class AlreadyExistingChampionship(val name: String) : ApplicationError()
}
