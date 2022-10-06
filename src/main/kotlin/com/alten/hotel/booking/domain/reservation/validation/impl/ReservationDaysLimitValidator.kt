package com.alten.hotel.booking.domain.reservation.validation.impl

import com.alten.hotel.booking.commons.logger.logInfo
import com.alten.hotel.booking.domain.reservation.entities.Reservation
import com.alten.hotel.booking.domain.reservation.validation.ReservationValidator
import java.time.temporal.ChronoUnit

object ReservationDaysLimitValidator : ReservationValidator {
    private const val DAYS_LIMIT = 3
    override val errorMessage: String = "The maximum allowed days in a reservation is $DAYS_LIMIT"

    override suspend fun isValid(reservation: Reservation): Boolean = with(reservation) {
        logInfo("Checking the reservation duration limit")
        ChronoUnit.DAYS.between(startDate, endDate) < DAYS_LIMIT
    }.also {
        logInfo("Returning $it")
    }
}
