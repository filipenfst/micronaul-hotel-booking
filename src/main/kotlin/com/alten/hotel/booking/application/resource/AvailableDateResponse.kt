package com.alten.hotel.booking.application.resource

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.serde.annotation.Serdeable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

@Serdeable
data class AvailableDateResponse(
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate
)
