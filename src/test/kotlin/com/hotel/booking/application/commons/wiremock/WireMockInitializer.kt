package com.hotel.booking.application.commons.wiremock

import com.hotel.booking.application.commons.conteiners.ContextInitializer

object WireMockInitializer : ContextInitializer {
    private var startWireMock = false

    val teamsApi = MockComponent()

    fun resetAll() {
        teamsApi.reset()
    }

    override fun getProperties(): Map<String, String> {
        return mapOf(
            "client.teams.api.base-url" to " http://localhost:%s".format(teamsApi.port()),
            "client.teams.api.retry-period" to "100"
        )
    }

    override fun start() {
        if (!startWireMock) {
            teamsApi.start()
            startWireMock = true
        }
    }

    override fun stop() {
        teamsApi.stop()
    }
}
