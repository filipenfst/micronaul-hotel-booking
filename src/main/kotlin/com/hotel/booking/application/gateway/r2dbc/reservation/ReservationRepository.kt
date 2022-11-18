package com.hotel.booking.application.gateway.r2dbc.reservation

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository
import org.reactivestreams.Publisher
import java.time.LocalDate
import java.util.UUID

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface ReservationRepository : ReactiveStreamsCrudRepository<ReservationEntity, UUID> {

    fun existsByStartDateBetweenOrEndDateBetween(
        startDateStart: LocalDate,
        startDateEnd: LocalDate,
        endDateStart: LocalDate,
        endDateEnd: LocalDate
    ): Publisher<Boolean>


    fun findByStartDateBetweenOrEndDateBetweenOrderByStartDate(
        startDateStart: LocalDate,
        startDateEnd: LocalDate,
        endDateStart: LocalDate,
        endDateEnd: LocalDate
    ): Publisher<ReservationEntity>

    override fun findById(id: UUID): Publisher<ReservationEntity>

    override fun <S : ReservationEntity> save(entity: S): Publisher<S>

    override fun deleteById(id: UUID): Publisher<Long>
}
