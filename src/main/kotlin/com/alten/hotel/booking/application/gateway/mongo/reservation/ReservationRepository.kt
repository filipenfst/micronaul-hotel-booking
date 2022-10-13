package com.alten.hotel.booking.application.gateway.mongo.reservation

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository
import org.reactivestreams.Publisher
import java.time.LocalDate
import java.util.UUID

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface ReservationRepository : ReactiveStreamsCrudRepository<ReservationDocument, UUID> {

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
    ): Publisher<ReservationDocument>

    override fun findById(id: UUID): Publisher<ReservationDocument>

    override fun <S : ReservationDocument> save(entity: S): Publisher<S>

    override fun deleteById(id: UUID): Publisher<Long>
}
