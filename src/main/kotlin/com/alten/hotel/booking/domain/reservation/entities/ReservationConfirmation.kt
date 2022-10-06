package com.alten.hotel.booking.domain.reservation.entities

import java.time.LocalDateTime

data class ReservationConfirmation(
    val id: String,
    val date: LocalDateTime
)
