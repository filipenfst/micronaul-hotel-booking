package com.hotel.booking.application

import com.hotel.booking.application.commons.conteiners.PostgresqlInitializer
import com.hotel.booking.application.commons.conteiners.merge
import com.hotel.booking.application.commons.wiremock.WireMockInitializer
import com.hotel.booking.application.gateway.r2dbc.reservation.ReservationRepository
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.util.*

private val initializers = setOf(
    WireMockInitializer,
    PostgresqlInitializer
).merge()

@MicronautTest(application = Application::class, transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal interface IntegrationTests : TestPropertyProvider {
    @BeforeAll
    fun setupTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }


    @AfterEach
    fun reset(
        reservationRepository: ReservationRepository
    ): Unit = runBlocking {
        WireMockInitializer.resetAll()
        reservationRepository.deleteAll().asFlow().collect {}
    }

    override fun getProperties(): Map<String, String> {
        initializers.start()
        return initializers.getProperties()
    }
}
