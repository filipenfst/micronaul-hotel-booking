package com.hotel.booking.application.resource.reservation

import com.hotel.booking.application.resource.reservation.dto.AvailableDateResponse
import com.hotel.booking.application.resource.reservation.dto.ReservationConfirmationResponse
import com.hotel.booking.application.resource.reservation.dto.ReservationRequestDTO
import com.hotel.booking.application.resource.reservation.dto.toReservationConfirmation
import com.hotel.booking.usecase.reservation.ListAvailabilityUseCase
import com.hotel.booking.usecase.reservation.ReservationBookingUseCase
import com.hotel.booking.usecase.reservation.ReservationCancellationUseCase
import com.hotel.booking.usecase.reservation.ReservationEditingUseCase
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.QueryValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.transaction.Transactional

@Controller("/reservations")
open class ReservationController(
    private val reservationBookingUseCase: ReservationBookingUseCase,
    private val reservationEditingUseCase: ReservationEditingUseCase,
    private val reservationCancellationUseCase: ReservationCancellationUseCase,
    private val listAvailabilityUseCase: ListAvailabilityUseCase,
) {

    @Post(produces = [io.micronaut.http.MediaType.APPLICATION_JSON])
    suspend fun create(
        @Body body: ReservationRequestDTO,
        @Header("X_CLIENT_ID") clientId: String
    ): ReservationConfirmationResponse = with(body) {
        reservationBookingUseCase.execute(clientId = clientId, startDate = start, endDate = end)
            .toReservationConfirmation()
    }

    @Put("/{reservationId}", produces = [io.micronaut.http.MediaType.APPLICATION_JSON])
    @Transactional
    open suspend fun edit(
        @Body body: ReservationRequestDTO,
        @Header("X_CLIENT_ID") clientId: String,
        @PathVariable reservationId: String,
    ): ReservationConfirmationResponse = with(body) {
        reservationEditingUseCase.execute(
            reservationId = reservationId,
            clientId = clientId,
            startDate = start,
            endDate = end
        ).toReservationConfirmation()
    }

    @Delete("/{reservationId}", produces = [io.micronaut.http.MediaType.APPLICATION_JSON])
    suspend fun cancel(
        @PathVariable reservationId: String,
        @Header("X_CLIENT_ID") clientId: String,
    ): HttpResponse<Any> {
        reservationCancellationUseCase.execute(reservationId, clientId)
        return HttpResponse.noContent()
    }

    @Get("/availability", produces = [io.micronaut.http.MediaType.APPLICATION_JSON])
    fun checkAvailability(
        @QueryValue(defaultValue = "30") days: Long
    ): Flow<AvailableDateResponse> {
        return listAvailabilityUseCase.execute(days).map { AvailableDateResponse(it) }
    }
}

