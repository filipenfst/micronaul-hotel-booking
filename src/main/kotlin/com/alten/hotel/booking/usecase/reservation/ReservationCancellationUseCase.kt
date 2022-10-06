package com.alten.hotel.booking.usecase.reservation

import com.alten.hotel.booking.commons.logger.logInfo
import com.alten.hotel.booking.domain.reservation.services.ReservationCancellationService

class ReservationCancellationUseCase(
    private val reservationCancellationService: ReservationCancellationService,
) {
    suspend fun execute(reservationId: String, clientId: String) {
        reservationCancellationService.cancel(reservationId, clientId)
        logInfo("Reservation canceled successfully.")
    }
}

