package com.alten.hotel.booking.application.resource

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDate
import java.time.LocalDateTime

@Serdeable
data class ReservationConfirmationDTO(
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val start: LocalDate,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val end: LocalDate,
    val confirmationId: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val reservationDate: LocalDateTime
)
