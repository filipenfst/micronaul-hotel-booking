package com.hotel.booking.usecase.reservation

import com.hotel.booking.commons.logger.logInfo
import com.hotel.booking.domain.reservation.services.ReservationCancellationService

class ReservationCancellationUseCase(
    private val reservationCancellationService: ReservationCancellationService,
) {
    suspend fun execute(reservationId: String, clientId: String) {
        reservationCancellationService.cancel(reservationId, clientId)
        logInfo("Reservation canceled successfully.")
    }
}

