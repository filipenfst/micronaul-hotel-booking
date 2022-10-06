package com.alten.hotel.booking.domain.reservation.validation

import com.alten.hotel.booking.domain.reservation.entities.Reservation
import com.alten.hotel.booking.domain.reservation.validation.exceptions.ReservationValidationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

interface ReservationValidator {
    val errorMessage: String
    suspend fun isValid(reservation: Reservation): Boolean
}


suspend fun Collection<ReservationValidator>.validate(reservation: Reservation) = coroutineScope {
    map {
        async {
            if (!it.isValid(reservation)) {
                throw ReservationValidationException("Reservation validation error: ${it.errorMessage}")
            }
        }
    }.awaitAll()
}
