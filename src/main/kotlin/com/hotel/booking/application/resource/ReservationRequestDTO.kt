package com.hotel.booking.application.resource

import com.hotel.booking.domain.reservation.entities.ConfirmedReservation
import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDate

@Serdeable
data class ReservationRequestDTO(
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val start: LocalDate,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val end: LocalDate
)

fun ConfirmedReservation.toReservationConfirmation() = ReservationConfirmationDTO(
    start = startDate,
    end = endDate,
    confirmationId = confirmation.id,
    reservationDate = confirmation.date
)
