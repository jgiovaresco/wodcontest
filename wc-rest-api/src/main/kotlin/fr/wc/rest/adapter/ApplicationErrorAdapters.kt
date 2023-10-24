package fr.wc.rest.adapter

import fr.wc.core.error.*
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.web.ErrorResponse


class ApplicationErrorResponse(private val applicationError: ApplicationError) : ErrorResponse {
    override fun getStatusCode(): HttpStatusCode = when (applicationError) {
        is ValidationError -> HttpStatus.BAD_REQUEST
        // Not Found
        is ChampionshipNotFound -> HttpStatus.NOT_FOUND
        // Precondition Failed
        is AthleteNotFound -> HttpStatus.PRECONDITION_FAILED
        is EventNotFound -> HttpStatus.PRECONDITION_FAILED
        is NotEnoughAthlete -> HttpStatus.PRECONDITION_FAILED
        is NoEvent -> HttpStatus.PRECONDITION_FAILED
        is IncorrectScoreType -> HttpStatus.BAD_REQUEST
        is AlreadyExistingChampionship -> HttpStatus.PRECONDITION_FAILED
    }

    override fun getBody(): ProblemDetail {


        return when (applicationError) {
            is IncorrectInput -> ProblemDetail.forStatus(getStatusCode()).apply { applicationError.errors.forEach { setProperty(it.field, it.errors.all) } }
            is AlreadyExistingChampionship -> ProblemDetail.forStatusAndDetail(getStatusCode(), "Name already exists").apply { setProperty("name", applicationError.name) }
            is AthleteNotFound -> ProblemDetail.forStatusAndDetail(getStatusCode(), "Athlete not found").apply { setProperty("id", applicationError.id.id) }
            is ChampionshipNotFound -> ProblemDetail.forStatus(getStatusCode()).apply {
                setProperty("resourceId", applicationError.id.value)
                setProperty("resourceType", "championship")
            }
            is EventNotFound -> ProblemDetail.forStatusAndDetail(getStatusCode(), "Event not found")
            is NoEvent -> ProblemDetail.forStatusAndDetail(getStatusCode(), "No event")
            is NotEnoughAthlete -> ProblemDetail.forStatusAndDetail(getStatusCode(), "Not enough athlete")
            is IncorrectScoreType -> ProblemDetail.forStatusAndDetail(getStatusCode(), "Incorrect score type")
        }
    }
}
