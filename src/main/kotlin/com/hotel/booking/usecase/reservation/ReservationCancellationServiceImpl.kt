package com.hotel.booking.usecase.reservation

import com.hotel.booking.domain.reservation.services.ReservationCancellationService
import com.hotel.booking.domain.reservation.services.ReservationGateway
import com.hotel.booking.domain.reservation.validation.exceptions.ReservationNotFoundException
import com.hotel.booking.domain.reservation.validation.exceptions.ReservationValidationException

class ReservationCancellationServiceImpl(
    private val reservationGateway: ReservationGateway,
) : ReservationCancellationService {
    override suspend fun cancel(reservationId: String, clientId: String) {
        val oldReservation = reservationGateway.find(reservationId)
            ?: throw ReservationNotFoundException("The requested reservation was not found.")


        if (oldReservation.clientId != clientId) {
            throw ReservationValidationException("The requested reservation does not belong to the provided user")
        }

        reservationGateway.remove(reservationId)
    }
}
