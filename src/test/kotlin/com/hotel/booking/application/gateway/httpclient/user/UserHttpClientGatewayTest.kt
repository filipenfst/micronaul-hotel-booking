package com.hotel.booking.application.gateway.httpclient.user

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.hotel.booking.application.IntegrationTests
import com.hotel.booking.application.commons.wiremock.WireMockInitializer
import com.hotel.booking.application.commons.wiremock.withJsonResponseFile
import com.hotel.booking.commons.logger.logError
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class UserHttpClientGatewayTest : IntegrationTests {

    @Inject
    private lateinit var userWebClientGateway: UserHttpClientGateway

    @Test
    fun testReturnUserSuccessfully(): Unit = runBlocking {
        WireMockInitializer.wireMock.stubFor(
            get(urlEqualTo("/user/123"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withJsonResponseFile("userResponse.json") { }
                )
        )

        userWebClientGateway.findUser("123")//.assertThat().isEqualTo(User("123", "Name test"))

        WireMockInitializer.wireMock.verify(
            1,
            getRequestedFor(urlMatching("/user/.*"))
        )
    }

    @Test
    fun testCircuitBreaker(): Unit = runBlocking {
        WireMockInitializer.wireMock.stubFor(
            get(urlEqualTo("/user/123"))
                .willReturn(
                    aResponse()
                        .withStatus(404)
                        .withJsonResponseFile("userResponse.json") { }
                )
        )

        repeat(3) {
            kotlin.runCatching {
                userWebClientGateway.findUser("123")
            }.onFailure {
                logError("test error", ex = it)
            }
        }

        WireMockInitializer.wireMock.verify(
            2,
            getRequestedFor(urlMatching("/user/.*"))
        )
    }
}
