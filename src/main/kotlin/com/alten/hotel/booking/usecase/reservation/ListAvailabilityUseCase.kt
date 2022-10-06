package com.alten.hotel.booking.usecase.reservation

import com.alten.hotel.booking.domain.reservation.services.ReservationGateway
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class ListAvailabilityUseCase(
    private val reservationGateway: ReservationGateway
) {

    fun execute(days: Long): Flow<LocalDate> {
        val startDate = LocalDate.now().plusDays(1)
        return reservationGateway.listAvailableDates(startDate, startDate.plusDays(days - 1))
    }
}
