package com.alten.hotel.booking.application.gateway.mongo.reservation

import com.alten.hotel.booking.application.IntegrationTests
import com.alten.hotel.booking.application.commons.assertThat
import com.alten.hotel.booking.domain.reservation.entities.ReservationRequest
import com.alten.hotel.booking.domain.reservation.validation.exceptions.ReservationNotFoundException
import jakarta.inject.Inject
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.util.UUID

internal class ReservationGatewayImplTest : IntegrationTests {
    @Inject
    private lateinit var classUnderTest: ReservationGatewayImpl

    @Inject
    private lateinit var repository: ReservationRepository

    @Test
    fun `Test is available returns false`(): Unit = runBlocking {
        classUnderTest.create(
            ReservationRequest(
                clientId = UUID.randomUUID().toString(),
                startDate = LocalDate.now().plusDays(1),
                endDate = LocalDate.now().plusDays(3),
            )
        )

        classUnderTest.isAvailable(
            startDate = LocalDate.now().plusDays(3),
            endDate = LocalDate.now().plusDays(7)
        ).let(Assertions::assertThat).isFalse()
    }

    @Test
    fun `Test is available returns true`(): Unit = runBlocking {
        classUnderTest.create(
            ReservationRequest(
                clientId = UUID.randomUUID().toString(),
                startDate = LocalDate.now().plusDays(1),
                endDate = LocalDate.now().plusDays(3),
            )
        )

        classUnderTest.isAvailable(
            startDate = LocalDate.now().plusDays(4),
            endDate = LocalDate.now().plusDays(7)
        ).let(Assertions::assertThat).isTrue()
    }

    @Test
    fun `Test is removing successfully`(): Unit = runBlocking {
        val reservation = classUnderTest.create(
            ReservationRequest(
                clientId = UUID.randomUUID().toString(),
                startDate = LocalDate.now().plusDays(1),
                endDate = LocalDate.now().plusDays(3),
            )
        )

        classUnderTest.remove(
            reservation.confirmation.id
        )

        repository.findAll().asFlow().assertThat { hasSize(0) }
    }

    @Test
    fun `Test is removing when dont exists`(): Unit = runBlocking {
        classUnderTest.create(
            ReservationRequest(
                clientId = UUID.randomUUID().toString(),
                startDate = LocalDate.now().plusDays(1),
                endDate = LocalDate.now().plusDays(3),
            )
        )

        assertThrows<ReservationNotFoundException> {
            classUnderTest.remove(UUID.randomUUID().toString())
        }

        repository.findAll().asFlow().assertThat { hasSize(1) }
    }

    @Test
    fun `Test list available dates correctly`(): Unit = runBlocking {
        val now = LocalDate.now()
        classUnderTest.create(
            ReservationRequest(
                clientId = UUID.randomUUID().toString(),
                startDate = now.plusDays(3),
                endDate = now.plusDays(5),
            )
        )
        classUnderTest.create(
            ReservationRequest(
                clientId = UUID.randomUUID().toString(),
                startDate = now.plusDays(9),
                endDate = now.plusDays(11),
            )
        )


        classUnderTest.listAvailableDates(now, now.plusDays(15))
            .assertThat { hasSize(10) }
            .isEqualTo(
                listOf(
                    now,
                    now.plusDays(1),
                    now.plusDays(2),
                    now.plusDays(6),
                    now.plusDays(7),
                    now.plusDays(8),
                    now.plusDays(12),
                    now.plusDays(13),
                    now.plusDays(14),
                    now.plusDays(15),
                )
            )
    }
}
