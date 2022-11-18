package com.hotel.booking.domain.reservation.entities

import java.time.LocalDate

data class ConfirmedReservation(
    override val clientId: String,
    override val startDate: LocalDate,
    override val endDate: LocalDate,
    val confirmation: ReservationConfirmation
) : Reservation
