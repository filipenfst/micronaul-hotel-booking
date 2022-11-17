package com.alten.hotel.booking.application.gateway.r2dbc.reservation

import com.alten.hotel.booking.domain.reservation.entities.ConfirmedReservation
import com.alten.hotel.booking.domain.reservation.entities.ReservationConfirmation
import com.alten.hotel.booking.domain.reservation.entities.ReservationRequest
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

const val RESERVATION_DB_NAME = "reservation"

@MappedEntity(RESERVATION_DB_NAME)
data class ReservationEntity(
    @field:Id
    val id: UUID = UUID.randomUUID(),
    val clientId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: LocalDateTime,
) {

    fun toDomain() = ConfirmedReservation(
        clientId = clientId,
        startDate = startDate,
        endDate = endDate,
        confirmation = ReservationConfirmation(
            id.toString(),
            createdAt
        )
    )
}

fun ReservationRequest.toEntity() = ReservationEntity(
    clientId = clientId,
    startDate = startDate,
    endDate = endDate,
    createdAt = LocalDateTime.now(),
)
