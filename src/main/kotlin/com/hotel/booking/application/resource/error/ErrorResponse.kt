package com.hotel.booking.application.resource.error

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class ErrorResponse(
    val message: String
)
