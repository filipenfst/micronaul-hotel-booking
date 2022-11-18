package com.hotel.booking.domain.reservation.services

import com.hotel.booking.domain.reservation.entities.ReservationRequest
import com.hotel.booking.domain.reservation.validation.ReservationValidator
import com.hotel.booking.domain.reservation.validation.validate
import java.time.LocalDate

class ReservationFactory(
    private val validators: Collection<ReservationValidator>
) {
    suspend fun create(clientId: String, startDate: LocalDate, endDate: LocalDate) = ReservationRequest(
        clientId = clientId,
        startDate = startDate,
        endDate = endDate,
    ).also {
        validators.validate(reservation = it)
    }
}
