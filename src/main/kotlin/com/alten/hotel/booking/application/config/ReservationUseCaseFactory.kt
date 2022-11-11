package com.alten.hotel.booking.application.config

import com.alten.hotel.booking.domain.reservation.services.ReservationFactory
import com.alten.hotel.booking.domain.reservation.services.ReservationGateway
import com.alten.hotel.booking.domain.reservation.validation.ReservationValidator
import com.alten.hotel.booking.domain.reservation.validation.impl.AdvanceReservationLimitValidator
import com.alten.hotel.booking.domain.reservation.validation.impl.ReservationDaysLimitValidator
import com.alten.hotel.booking.domain.reservation.validation.impl.StartAfterBeginningValidator
import com.alten.hotel.booking.domain.reservation.validation.impl.StartDateValidator
import com.alten.hotel.booking.usecase.reservation.AvailabilityValidator
import com.alten.hotel.booking.usecase.reservation.ListAvailabilityUseCase
import com.alten.hotel.booking.usecase.reservation.ReservationBookingUseCase
import com.alten.hotel.booking.usecase.reservation.ReservationCancellationServiceImpl
import com.alten.hotel.booking.usecase.reservation.ReservationCancellationUseCase
import com.alten.hotel.booking.usecase.reservation.ReservationEditingUseCase
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import jakarta.inject.Named
import jakarta.inject.Singleton

@Factory
class ReservationUseCaseFactory {

    /**
     * JVM Memory metrics bean.
     *
     * @return jvmMemoryMetrics
     */
    @Bean
    @Primary
    @Singleton
    fun jvmMemoryMetrics() = JvmMemoryMetrics()

    @Singleton
    fun availabilityValidator(reservationGateway: ReservationGateway) = AvailabilityValidator(
        reservationGateway = reservationGateway
    )

    @Singleton
    @Named("reservationBookingValidatorLists")
    fun reservationBookingValidatorLists(
        reservationFactory: AvailabilityValidator
    ): List<ReservationValidator> = listOf(
        ReservationDaysLimitValidator,
        AdvanceReservationLimitValidator,
        StartAfterBeginningValidator,
        StartDateValidator,
        reservationFactory,
    )

    @Singleton
    fun reservationFactory(
        @Named("reservationBookingValidatorLists") validators: List<ReservationValidator>
    ) = ReservationFactory(
        validators = validators
    )

    @Singleton
    fun reservationBookingUseCase(
        reservationFactory: ReservationFactory,
        reservationGateway: ReservationGateway,
    ) = ReservationBookingUseCase(
        reservationGateway = reservationGateway,
        reservationFactory = reservationFactory,
    )

    @Singleton
    fun reservationCancellationServiceImpl(
        reservationGateway: ReservationGateway
    ) = ReservationCancellationServiceImpl(
        reservationGateway = reservationGateway,
    )

    @Singleton
    fun reservationEditingUseCase(
        reservationCancellationService: ReservationCancellationServiceImpl,
        reservationFactory: ReservationFactory,
        reservationGateway: ReservationGateway,
    ) = ReservationEditingUseCase(
        reservationGateway = reservationGateway,
        reservationFactory = reservationFactory,
        reservationCancellationService = reservationCancellationService
    )

    @Singleton
    fun reservationCancellationUseCase(
        reservationCancellationService: ReservationCancellationServiceImpl
    ) = ReservationCancellationUseCase(
        reservationCancellationService = reservationCancellationService,
    )

    @Singleton
    fun listAvailabilityUseCase(reservationGateway: ReservationGateway) = ListAvailabilityUseCase(
        reservationGateway = reservationGateway,
    )
}
