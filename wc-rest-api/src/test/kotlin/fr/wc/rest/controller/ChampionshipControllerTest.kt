package fr.wc.rest.controller

import fr.wc.core.model.championship.ChampionshipBuilder
import fr.wc.core.model.femaleRXDivision
import fr.wc.core.model.maleRXDivision
import fr.wc.core.repository.ChampionshipRepository
import fr.wc.rest.model.CreateChampionshipRequest
import fr.wc.rest.model.DivisionDto
import fr.wc.rest.model.genderName
import fr.wc.rest.model.levelName
import io.kotest.common.runBlocking
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.core.StringRegularExpression.matchesRegex
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import java.time.LocalDate


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChampionshipControllerTest(
        @LocalServerPort private val localServerPort: Int
) {

    @Autowired
    lateinit var championshipRepository: ChampionshipRepository

    @BeforeEach
    fun setup() {
        RestAssured.port = localServerPort
    }

    @Nested
    inner class CreateChampionship {
        @Test
        fun `should provide the url getting the created championship in 'Location' header`() {
            Given {
                contentType("application/json")
                body(CreateChampionshipRequest("Championship 1", LocalDate.now().toString(), listOf(DivisionDto("Male", "RX"), DivisionDto("Female", "RX"))))
            } When {
                post("/championships")
            } Then {
                statusCode(201)
                header("Location", matchesRegex("http://localhost:$localServerPort/championships/([a-f\\d-]+)"))
            }
        }

        @Test
        fun `should return 400 response when required element missing`() {
            Given {
                contentType("application/json")
                body("""{}""")
            } When {
                post("/championships")
            } Then {
                statusCode(400)
                body("title", equalTo("Bad Request"))
                body("detail", equalTo("Invalid request content."))
                body("errors.name.code", hasItems("NotBlank"))
                body("errors.name.message", hasItems("Name is required"))
                body("errors.date.code", hasItems("NotBlank"))
                body("errors.date.message", hasItems("Date is required"))
                body("errors.divisions.code", hasItems("NotNull"))
                body("errors.divisions.message", hasItems("Divisions is required"))
            }
        }

        // TODO fun `should return 400 response when date in past`() {}
        // TODO fun `should return 400 response when name already exists`() {}
        // TODO fun `should return 400 response when name already exists`() {}
    }

    @Nested
    inner class GetChampionship {
        @BeforeEach
        fun setUp() {
            runBlocking {
                championshipRepository.save(
                        ChampionshipBuilder.aChampionship(
                                id = "created",
                                name = "Championship 1",
                                date = LocalDate.now(),
                                divisions = listOf(
                                        maleRXDivision(),
                                        femaleRXDivision()
                                )
                        ).created()
                )
            }
        }

        @Test
        fun `should return a created championship`() {
            Given {
                accept(MediaType.ALL_VALUE)
            } When {
                get("/championships/created")
            } Then {
                log().all()
                statusCode(200)
                body("id", equalTo("created"))
                body("name", equalTo("Championship 1"))
                body("date", equalTo(LocalDate.now().toString()))
                body("status", equalTo("Created"))
                body("divisions.gender", hasItems(genderName(maleRXDivision()), genderName(femaleRXDivision())))
                body("divisions.level", hasItems(levelName(maleRXDivision()), levelName(femaleRXDivision())))
                body("_links.self.href", equalTo("http://localhost:$localServerPort/championships/created"))
            }
        }

        @Test
        fun `should return 404 response when no championship found`() {
            Given {
                accept(MediaType.ALL_VALUE)
            } When {
                get("/championships/unknown")
            } Then {
                statusCode(404)
                body("title", equalTo("Not Found"))
                body("resourceId", equalTo("unknown"))
                body("resourceType", equalTo("championship"))
            }
        }

    }
}
