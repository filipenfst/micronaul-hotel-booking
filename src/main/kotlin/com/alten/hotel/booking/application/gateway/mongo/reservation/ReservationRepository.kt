package com.alten.hotel.booking.application.gateway.mongo.reservation

import io.micronaut.data.mongodb.annotation.MongoRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.reactivestreams.Publisher
import java.time.LocalDate
import javax.transaction.Transactional

@MongoRepository
interface ReservationRepository : ReactiveStreamsCrudRepository<ReservationDocument, String> {

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

    override fun findById(id: String): Publisher<ReservationDocument>

    override fun <S : ReservationDocument> save(entity: S): Publisher<S>

    override fun deleteById(id: String): Publisher<Long>
}
