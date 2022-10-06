package com.alten.hotel.booking.application.resource

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class ErrorResponse(
    val message: String
)
