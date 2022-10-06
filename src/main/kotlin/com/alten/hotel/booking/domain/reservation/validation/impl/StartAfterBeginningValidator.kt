package com.alten.hotel.booking.domain.reservation.validation.impl

import com.alten.hotel.booking.commons.logger.logInfo
import com.alten.hotel.booking.domain.reservation.entities.Reservation
import com.alten.hotel.booking.domain.reservation.validation.ReservationValidator
import java.time.temporal.ChronoUnit

object StartAfterBeginningValidator : ReservationValidator {
    override val errorMessage: String = "The end date should be later than one day after the beginning date"

    override suspend fun isValid(reservation: Reservation): Boolean = with(reservation) {
        logInfo("Checking the endDate is equal of after the startDate")
        ChronoUnit.DAYS.between(startDate, endDate) >= 0
    }.also {
        logInfo("Returning $it")
    }
}

