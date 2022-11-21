package com.hotel.booking.application.resource.reservation.dto

import com.hotel.booking.domain.reservation.entities.ConfirmedReservation
import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDate

@Serdeable
data class ReservationRequest(
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val start: LocalDate,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val end: LocalDate
)

fun ConfirmedReservation.toReservationConfirmation() = ReservationConfirmationResponse(
    start = startDate,
    end = endDate,
    confirmationId = confirmation.id,
    reservationDate = confirmation.date
)
