package com.alten.hotel.booking.domain.reservation.validation.impl

import com.alten.hotel.booking.commons.logger.logInfo
import com.alten.hotel.booking.domain.reservation.entities.Reservation
import com.alten.hotel.booking.domain.reservation.validation.ReservationValidator
import java.time.LocalDate

object StartDateValidator : ReservationValidator {
    override val errorMessage = "The start date on reservation needs to be at least one day after the reservation."

    override suspend fun isValid(reservation: Reservation): Boolean = with(reservation) {
        logInfo("Checking the startDate is not current date")
        startDate.isAfter(LocalDate.now())
    }.also {
        logInfo("Returning $it")
    }
}
