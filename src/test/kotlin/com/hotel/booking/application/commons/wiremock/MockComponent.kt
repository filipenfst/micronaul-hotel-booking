package com.hotel.booking.application.commons.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration

class MockComponent {

    val wireMock = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort().dynamicHttpsPort())

    fun start() = wireMock.start()
    fun stop() = wireMock.stop()

    fun reset() = wireMock.resetAll()

    fun port() = wireMock.port()
}
