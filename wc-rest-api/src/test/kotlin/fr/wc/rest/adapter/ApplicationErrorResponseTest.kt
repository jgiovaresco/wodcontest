package fr.wc.rest.adapter

import arrow.core.nonEmptyListOf
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import fr.wc.core.InvalidName
import fr.wc.core.error.*
import fr.wc.core.model.AthleteId
import fr.wc.core.model.ChampionshipId
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ApplicationErrorResponseTest : FunSpec({

    context("StatusCode") {
        withData(
                nameFn = { "${it.first.javaClass} should have HTTP status code ${it.second}" },
                Pair(IncorrectInput(nonEmptyListOf(InvalidName(nonEmptyListOf("error")))), 400),
                Pair(ChampionshipNotFound(ChampionshipId("id")), 404),
                Pair(AthleteNotFound(AthleteId("id")), 412),
                Pair(EventNotFound, 412),
                Pair(NotEnoughAthlete, 412),
                Pair(NoEvent, 412),
                Pair(IncorrectScoreType, 400),
                Pair(AlreadyExistingChampionship("name"), 412),
        ) { (error, expected) ->
            ApplicationErrorResponse(error).getStatusCode().value() shouldBe expected
        }
    }

    context("Body") {
        withData(
                nameFn = { "${it.first.javaClass} should have body ${it.second}" },
                Pair(IncorrectInput(nonEmptyListOf(InvalidName(nonEmptyListOf("an error")))), """{ "type":"about:blank", "title":"Bad Request", "status":400, "properties": { "name": ["an error"] } }"""),
                Pair(AlreadyExistingChampionship("my name"), """{ "type":"about:blank", "title":"Precondition Failed", "status":412, "properties": { "name": "my name" }, "detail": "Name already exists" }"""),
                Pair(ChampionshipNotFound(ChampionshipId("an-id")), """{ "type":"about:blank", "title":"Not Found", "status":404, "properties": { "id": "an-id" } }"""),
                Pair(AthleteNotFound(AthleteId("an-id")), """{ "type":"about:blank", "title":"Precondition Failed", "status":412, "properties": { "id": "an-id" }, "detail": "Athlete not found" }"""),
                Pair(EventNotFound, """{ "type":"about:blank", "title":"Precondition Failed", "status":412, "detail": "Event not found" }"""),
                Pair(NoEvent, """{ "type":"about:blank", "title":"Precondition Failed", "status":412, "detail": "No event" }"""),
                Pair(NotEnoughAthlete, """{ "type":"about:blank", "title":"Precondition Failed", "status":412, "detail": "Not enough athlete" }"""),
                Pair(IncorrectScoreType, """{ "type":"about:blank", "title":"Bad Request", "status":400, "detail": "Incorrect score type" }"""),
        ) { (error, expected) ->
            val objectMapper = jacksonMapperBuilder()
                    .serializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
                    .build()
            val result = ApplicationErrorResponse(error)
            objectMapper.writeValueAsString(result.body) shouldEqualJson expected
        }
    }
})
