package com.hotel.booking.application.resource.error

import com.hotel.booking.commons.logger.logWarn
import com.hotel.booking.domain.reservation.validation.exceptions.ReservationNotFoundException
import com.hotel.booking.domain.reservation.validation.exceptions.ReservationValidationException
import io.micronaut.context.annotation.Factory
import io.micronaut.http.HttpResponse
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton

@Factory
class ExceptionHandlerFactory {
    @Singleton
    fun reservationValidationExceptionHandler() =
        ExceptionHandler<ReservationValidationException, HttpResponse<ErrorResponse>> { request, exception ->
            logWarn("Error on request $request", ex = exception)
            HttpResponse.badRequest(ErrorResponse(exception.message))
        }

    @Singleton
    fun reservationNotFoundException() =
        ExceptionHandler<ReservationNotFoundException, HttpResponse<ErrorResponse>> { request, exception ->
            logWarn("Error on request $request", ex = exception)
            HttpResponse.notFound(ErrorResponse(exception.message))
        }
//    @Singleton
//    fun exceptionHandler() =
//        ExceptionHandler<Exception, HttpResponse<ErrorResponse>> { request, exception ->
//            logError("Error on request $request", ex = exception)
//            HttpResponse.notFound(ErrorResponse(exception.message ?: ""))
//        }
}

