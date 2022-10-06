package com.alten.hotel.booking.domain.reservation.services

interface ReservationCancellationService{
    suspend fun cancel(reservationId: String, clientId: String)
}
