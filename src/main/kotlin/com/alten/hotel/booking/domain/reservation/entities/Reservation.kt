package com.alten.hotel.booking.domain.reservation.entities

import java.time.LocalDate

sealed interface Reservation {
    val clientId: String
    val startDate: LocalDate
    val endDate: LocalDate
}

