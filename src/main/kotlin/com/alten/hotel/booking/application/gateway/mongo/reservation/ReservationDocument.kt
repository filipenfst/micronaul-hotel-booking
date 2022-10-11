package com.alten.hotel.booking.application.gateway.mongo.reservation

import com.alten.hotel.booking.domain.reservation.entities.ConfirmedReservation
import com.alten.hotel.booking.domain.reservation.entities.ReservationConfirmation
import com.alten.hotel.booking.domain.reservation.entities.ReservationRequest
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import org.bson.types.ObjectId
import java.time.LocalDate
import java.time.LocalDateTime

const val RESERVATION_DB_NAME = "reservation"

@MappedEntity(RESERVATION_DB_NAME)
data class ReservationDocument(
    @field:Id
    val id: String = ObjectId().toString(),
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
            id,
            createdAt
        )
    )
}

fun ReservationRequest.toDocument() = ReservationDocument(
    clientId = clientId,
    startDate = startDate,
    endDate = endDate,
    createdAt = LocalDateTime.now(),
)
