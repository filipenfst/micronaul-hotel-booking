package com.hotel.booking.application.resource

import com.hotel.booking.application.gateway.r2dbc.reservation.ReservationEntity
import com.hotel.booking.application.gateway.r2dbc.reservation.ReservationRepository
import com.hotel.booking.commons.logger.logInfo
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asFlow
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.system.measureTimeMillis

@Controller("/setup-reservation")
class SetupController(
    private val repository: ReservationRepository
) {

    @Delete
    suspend fun deleteALl() {
        repository.deleteAll().asFlow().collect()
    }


    @Post
    suspend fun generate(
        @QueryValue size: Long,
        @QueryValue batchSize: Int,
    ) {
        generateData(size).groupInBatches(batchSize).collect {
            repository.saveAll(it).asFlow().collect()
        }
    }


    private fun generateData(size: Long = 100000) = flow {
        measureTimeMillis {
            val now = LocalDate.now().minusDays(size + 1)

            val userId = "05b731f3-f1c7-4865-bc13-25520224fc1c"

            repeat(size.toInt()) {
                now.plusDays(it.toLong())
                    .run {
                        ReservationEntity(
                            clientId = userId,
                            startDate = this,
                            endDate = this,
                            createdAt = LocalDateTime.now()
                        )
                    }.run {
                        logInfo("Generating $it: $this")
                        emit(this)
                    }
            }
        }.also { println("Executer in: $it") }
    }

    private fun <T> Flow<T>.groupInBatches(batchSize: Int) = flow<List<T>> {
        val batch = mutableListOf<T>()
        collect { id ->
            batch.add(id)
            if (batch.size >= batchSize) {
                emit(batch)
                batch.clear()
            }
        }
        if (batch.isNotEmpty()) emit(batch)
    }
}
