package com.alten.hotel.booking.application.resource

import com.alten.hotel.booking.application.IntegrationTests
import com.alten.hotel.booking.application.commons.assertThat
import com.alten.hotel.booking.application.gateway.r2dbc.reservation.ReservationEntity
import com.alten.hotel.booking.application.gateway.r2dbc.reservation.ReservationRepository
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

internal class ReservationControllerTest : IntegrationTests {
    @Inject
    private lateinit var spec: RequestSpecification

    @Inject
    private lateinit var repository: ReservationRepository

    @Test
    fun `Test booking room successfully`(): Unit = runBlocking {
        val start = LocalDate.now().plusDays(2)
        val end = start.plusDays(2)

        val clientId = UUID.randomUUID().toString()
        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .header("X_CLIENT_ID", clientId)
            .body(
                """{
                "start":"$start",
                 "end":"$end"
                }"""
            ).`when`().post("/reservations")
            .then()
            .statusCode(200)
            .body(
                "start", CoreMatchers.`is`(start.toString()),
                "end", CoreMatchers.`is`(end.toString())
            )

        repository.findAll().asFlow().assertThat { hasSize(1) }
            .ignoringFields("id")
            .isEqualTo(
                listOf(
                    ReservationEntity(
                        clientId = clientId,
                        startDate = start,
                        endDate = end,
                        createdAt = LocalDateTime.now(),
                    )
                )
            )
    }

    @Test
    fun `Test booking room fails for more then 30 days advance`(): Unit = runBlocking {
        val start = LocalDate.now().plusDays(31)
        val end = start.plusDays(2)

        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .header("X_CLIENT_ID", UUID.randomUUID().toString())
            .body(
                """{
                "start":"$start",
                 "end":"$end"
                }"""
            ).`when`().post("/reservations")
            .then()
            .statusCode(400)
            .body(
                "message",
                CoreMatchers.`is`(
                    "Reservation validation error: The maximum allowed reservation is 30 days in advance"
                ),
            )

        repository.findAll().asFlow().assertThat { hasSize(0) }

    }

    @Test
    fun `Test booking room fails for more then 3 days reservation`(): Unit = runBlocking {
        val start = LocalDate.now().plusDays(1)
        val end = start.plusDays(3)

        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .header("X_CLIENT_ID", UUID.randomUUID().toString())
            .body(
                """{
                "start":"$start",
                 "end":"$end"
                }"""
            ).`when`().post("/reservations")
            .then()
            .statusCode(400)
            .body(
                "message",
                CoreMatchers.`is`(
                    "Reservation validation error: The maximum allowed days in a reservation is 3"
                ),
            )

        repository.findAll().asFlow().assertThat { hasSize(0) }
    }

    @Test
    fun `Test booking room fails for start date after end date`(): Unit = runBlocking {
        val start = LocalDate.now().plusDays(1)
        val end = start.minusDays(1)

        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .header("X_CLIENT_ID", UUID.randomUUID().toString())
            .body(
                """{
                "start":"$start",
                 "end":"$end"
                }"""
            ).`when`().post("/reservations")
            .then()
            .statusCode(400)
            .body(
                "message",
                CoreMatchers.`is`(
                    "Reservation validation error: The end date should be later than one day after the beginning date"
                ),
            )

        repository.findAll().asFlow().assertThat { hasSize(0) }
    }

    @Test
    fun `Test booking room fails for same day start date`(): Unit = runBlocking {
        val start = LocalDate.now()
        val end = start.plusDays(2)

        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .header("X_CLIENT_ID", UUID.randomUUID().toString())
            .body(
                """{
                "start":"$start",
                 "end":"$end"
                }"""
            ).`when`().post("/reservations")
            .then()
            .statusCode(400)
            .body(
                "message",
                CoreMatchers.`is`(
                    "Reservation validation error: The start date on reservation needs to be at least one day " +
                            "after the reservation."
                ),
            )

        repository.findAll().asFlow().assertThat { hasSize(0) }
    }

    @Test
    fun `Test booking room fails for unavailable days`(): Unit = runBlocking {
        val reservation = repository.save(
            ReservationEntity(
                clientId = UUID.randomUUID().toString(),
                startDate = LocalDate.now().plusDays(1),
                endDate = LocalDate.now().plusDays(3),
                createdAt = LocalDateTime.now(),
            )
        ).awaitSingle()

        val start = LocalDate.now().plusDays(1)
        val end = start.plusDays(2)

        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .header("X_CLIENT_ID", UUID.randomUUID().toString())
            .body(
                """{
                "start":"$start",
                 "end":"$end"
                }"""
            ).`when`().post("/reservations")
            .then()
            .statusCode(400)
            .body(
                "message",
                CoreMatchers.`is`(
                    "Reservation validation error: The provided date are not available"
                ),
            )
        repository.findAll().asFlow().assertThat { hasSize(1) }.isEqualTo(
            listOf(reservation)
        )
    }

    @Test
    fun `Test editing reservation room successfully`(): Unit = runBlocking {
        val reservation = repository.save(
            ReservationEntity(
                clientId = UUID.randomUUID().toString(),
                startDate = LocalDate.now().plusDays(1),
                endDate = LocalDate.now().plusDays(3),
                createdAt = LocalDateTime.now(),
            )
        ).awaitSingle()

        val start = LocalDate.now().plusDays(1)
        val end = start.plusDays(2)

        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .header("X_CLIENT_ID", reservation.clientId)
            .body(
                """{
                "start":"$start",
                 "end":"$end"
                }"""
            ).`when`().put("/reservations/${reservation.id}")
            .then()
            .statusCode(200)
            .body(
                "start", CoreMatchers.`is`(start.toString()),
                "end", CoreMatchers.`is`(end.toString())
            )

        repository.findAll().asFlow().assertThat { hasSize(1) }
            .ignoringFields("id")
            .isEqualTo(
                listOf(
                    ReservationEntity(
                        id = UUID.randomUUID(),
                        clientId = reservation.clientId,
                        startDate = start,
                        endDate = end,
                        createdAt = LocalDateTime.now(),
                    )
                )
            )

    }

    @Test
    fun `Test editing reservation fails `(): Unit = runBlocking {
        val reservation = repository.save(
            ReservationEntity(
                clientId = UUID.randomUUID().toString(),
                startDate = LocalDate.now().plusDays(1),
                endDate = LocalDate.now().plusDays(3),
                createdAt = LocalDateTime.now(),
            )
        ).awaitSingle()

        val start = LocalDate.now()
        val end = start.plusDays(2)

        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .header("X_CLIENT_ID", reservation.clientId)
            .body(
                """{
                "start":"$start",
                 "end":"$end"
                }"""
            ).`when`().put("/reservations/${reservation.id}")
            .then()
            .statusCode(400)
            .body(
                "message",
                CoreMatchers.`is`(
                    "Reservation validation error: The start date on reservation needs to be at least one day " +
                            "after the reservation."
                ),
            )

        repository.findAll().asFlow()
            .assertThat { hasSize(0) }
    }

    @Test
    fun `Test editing reservation room fails when reservation does not exists`(): Unit = runBlocking {
        val start = LocalDate.now().plusDays(1)
        val end = LocalDate.now().plusDays(4)

        val clientID = UUID.randomUUID().toString()
        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .header("X_CLIENT_ID", clientID)
            .body(
                """{
                "start":"$start",
                 "end":"$end"
                }"""
            ).`when`().put("/reservations/${UUID.randomUUID()}")
            .then()
            .statusCode(404)
            .body(
                "message",
                CoreMatchers.`is`(
                    "The requested reservation was not found."
                ),
            )

        repository.findAll().asFlow().assertThat { hasSize(0) }
    }

    @Test
    fun `Test removing reservation success`(): Unit = runBlocking {
        val reservation = repository.save(
            ReservationEntity(
                clientId = UUID.randomUUID().toString(),
                startDate = LocalDate.now().plusDays(1),
                endDate = LocalDate.now().plusDays(3),
                createdAt = LocalDateTime.now(),
            )
        ).awaitSingle()

        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .header("X_CLIENT_ID", reservation.clientId)
            .`when`().delete("/reservations/${reservation.id}")
            .then()
            .statusCode(204)

        repository.findAll().asFlow().assertThat { hasSize(0) }
    }

    @Test
    fun `Test removing reservation from other client`(): Unit = runBlocking {
        val reservation = repository.save(
            ReservationEntity(
                clientId = UUID.randomUUID().toString(),
                startDate = LocalDate.now().plusDays(1),
                endDate = LocalDate.now().plusDays(3),
                createdAt = LocalDateTime.now(),
            )
        ).awaitSingle()

        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .header("X_CLIENT_ID", UUID.randomUUID().toString())
            .`when`().delete("/reservations/${reservation.id}")
            .then()
            .statusCode(400)
            .body(
                "message",
                CoreMatchers.`is`(
                    "The requested reservation does not belong to the provided user"
                ),
            )

        repository.findAll().asFlow().assertThat { hasSize(1) }
    }

    @Test
    fun `Test removing reservation that does not exist`(): Unit = runBlocking {
        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .header("X_CLIENT_ID", UUID.randomUUID().toString())
            .`when`().delete("/reservations/${UUID.randomUUID()}")
            .then()
            .statusCode(404)
            .body(
                "message",
                CoreMatchers.`is`(
                    "The requested reservation was not found."
                ),
            )

        repository.findAll().asFlow().assertThat { hasSize(0) }
    }

    @Test
    fun `Testing check the availability return successfully`(): Unit = runBlocking {
        val now = LocalDate.now()
        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .queryParam("days", 10)
            .`when`().get("/reservations/availability")
            .then()
            .statusCode(200)
            .body(
                "[0].date", CoreMatchers.`is`(now.plusDays(1).toString()),
                "[9].date", CoreMatchers.`is`(now.plusDays(10).toString()),
            )
    }
}
