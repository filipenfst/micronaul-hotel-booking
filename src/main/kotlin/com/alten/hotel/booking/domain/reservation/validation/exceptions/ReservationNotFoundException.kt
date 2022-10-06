package com.alten.hotel.booking.domain.reservation.validation.exceptions

data class ReservationNotFoundException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause)
