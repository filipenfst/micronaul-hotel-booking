package com.alten.hotel.booking.usecase.reservation

import com.alten.hotel.booking.domain.reservation.entities.Reservation
import com.alten.hotel.booking.domain.reservation.services.ReservationGateway
import com.alten.hotel.booking.domain.reservation.validation.ReservationValidator

class AvailabilityValidator(
    private val reservationGateway: ReservationGateway,
) : ReservationValidator {
    override val errorMessage: String = "The provided date are not available"

    override suspend fun isValid(reservation: Reservation): Boolean = with(reservation) {
        return reservationGateway.isAvailable(startDate, endDate)
    }
}
