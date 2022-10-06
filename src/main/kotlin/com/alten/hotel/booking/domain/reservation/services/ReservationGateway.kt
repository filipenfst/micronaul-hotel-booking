package com.alten.hotel.booking.domain.reservation.services

import com.alten.hotel.booking.domain.reservation.entities.ConfirmedReservation
import com.alten.hotel.booking.domain.reservation.entities.ReservationRequest
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ReservationGateway {
    suspend fun isAvailable(startDate: LocalDate, endDate: LocalDate): Boolean
    suspend fun find(reservationId: String): ConfirmedReservation?
    suspend fun create(reservation: ReservationRequest): ConfirmedReservation
    suspend fun remove(reservationId: String)
    fun listAvailableDates(startDate: LocalDate, endDate: LocalDate): Flow<LocalDate>
}
