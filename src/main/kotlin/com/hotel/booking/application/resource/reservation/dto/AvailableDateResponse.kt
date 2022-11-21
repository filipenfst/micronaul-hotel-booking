package com.hotel.booking.application.resource.reservation.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDate

@Serdeable
data class AvailableDateResponse(
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate
)
