package com.hotel.booking.application.gateway.r2dbc.reservation

import com.hotel.booking.commons.logger.logInfo
import com.hotel.booking.commons.utils.date.rangeTo
import com.hotel.booking.commons.utils.date.until
import com.hotel.booking.domain.reservation.entities.ConfirmedReservation
import com.hotel.booking.domain.reservation.entities.ReservationRequest
import com.hotel.booking.domain.reservation.services.ReservationGateway
import com.hotel.booking.domain.reservation.validation.exceptions.ReservationNotFoundException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import java.time.LocalDate
import java.util.UUID

@Singleton
class ReservationGatewayImpl(
    @Inject private val repository: ReservationRepository,
) : ReservationGateway {

    override suspend fun isAvailable(startDate: LocalDate, endDate: LocalDate): Boolean {
        logInfo("Checking if dates $startDate to $endDate are available")
        return !repository.existsByStartDateBetweenOrEndDateBetween(startDate, endDate, startDate, endDate)
            .awaitSingle().also {
                logInfo("Returning $it")
            }
    }

    override suspend fun find(reservationId: String): ConfirmedReservation? {
        logInfo("Searching for reservation with id: $reservationId")
        return repository.findById(UUID.fromString(reservationId)).awaitFirstOrNull()?.toDomain().also {
            logInfo("Returning $it")
        }
    }

    override suspend fun create(reservation: ReservationRequest): ConfirmedReservation = with(reservation) {
        logInfo("Creating new reservation: $reservation")
        repository.save(toEntity()).awaitSingle().toDomain().also {
            logInfo("Returning $it")
        }
    }

    override suspend fun remove(reservationId: String) {
        logInfo("Removing reservation with id:$reservationId")
        if (repository.deleteById(UUID.fromString(reservationId)).awaitSingle() < 1L) {
            throw ReservationNotFoundException("The requested reservation with id:$reservationId was not found.").also {
                logInfo("Failed to remove reservation: ${it.message}")
            }
        }
        logInfo("Reservation removed successfully")
    }

    override fun listAvailableDates(startDate: LocalDate, endDate: LocalDate): Flow<LocalDate> = flow {
        var last = startDate
        repository.findByStartDateBetweenOrEndDateBetweenOrderByStartDate(startDate, endDate, startDate, endDate)
            .asFlow()
            .collect {
                for (date in last until it.startDate) {
                    emit(date)
                }
                last = it.endDate.plusDays(1)

            }

        for (date in last..endDate) {
            emit(date)
        }
    }
}
