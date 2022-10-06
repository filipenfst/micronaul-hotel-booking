package com.alten.hotel.booking.domain.reservation.validation.impl

import com.alten.hotel.booking.commons.logger.logInfo
import com.alten.hotel.booking.domain.reservation.entities.Reservation
import com.alten.hotel.booking.domain.reservation.validation.ReservationValidator
import java.time.LocalDate

object AdvanceReservationLimitValidator : ReservationValidator {
    private const val DAYS_LIMIT = 30L
    override val errorMessage: String = "The maximum allowed reservation is $DAYS_LIMIT days in advance"

    override suspend fun isValid(reservation: Reservation): Boolean = with(reservation) {
        logInfo("Checking if reservation date is beyond the $DAYS_LIMIT days limit")
        endDate <= LocalDate.now().plusDays(DAYS_LIMIT)
    }.also {
        logInfo("Returning $it")
    }
}
