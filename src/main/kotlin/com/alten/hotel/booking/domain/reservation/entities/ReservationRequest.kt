package com.alten.hotel.booking.domain.reservation.entities

import java.time.LocalDate

data class ReservationRequest(
    override val clientId: String,
    override val startDate: LocalDate,
    override val endDate: LocalDate
) : Reservation
