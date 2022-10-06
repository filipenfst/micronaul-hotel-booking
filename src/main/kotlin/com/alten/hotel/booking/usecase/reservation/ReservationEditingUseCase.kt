package com.alten.hotel.booking.usecase.reservation

import com.alten.hotel.booking.domain.reservation.entities.ConfirmedReservation
import com.alten.hotel.booking.domain.reservation.services.ReservationCancellationService
import com.alten.hotel.booking.domain.reservation.services.ReservationFactory
import com.alten.hotel.booking.domain.reservation.services.ReservationGateway
import java.time.LocalDate

class ReservationEditingUseCase(
    private val reservationCancellationService: ReservationCancellationService,
    private val reservationGateway: ReservationGateway,
    private val reservationFactory: ReservationFactory
) {
    suspend fun execute(
        reservationId: String,
        clientId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): ConfirmedReservation {
        reservationCancellationService.cancel(reservationId = reservationId, clientId = clientId)
        return reservationFactory.create(clientId = clientId, startDate = startDate, endDate = endDate)
            .let {
                reservationGateway.create(it)
            }
    }
}
