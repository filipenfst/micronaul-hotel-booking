package com.hotel.booking.domain.reservation.validation.exceptions

data class ReservationValidationException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause)
