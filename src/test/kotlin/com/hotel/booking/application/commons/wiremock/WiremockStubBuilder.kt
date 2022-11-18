package com.hotel.booking.application.commons.wiremock

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import wiremock.com.google.common.net.HttpHeaders

internal data class WiremockStubBuilder(
    private val mockComponent: MockComponent,
    private val requestMethod: RequestMethod,
    private val url: String,
    val token: String? = null
) {
    var jsonRequestBody: (() -> String)? = null
    var requestBuilder: (MappingBuilder.() -> MappingBuilder) = { this }
    var status: Int = 200
    var jsonResponseBody: (() -> String)? = null
    var responseBuilder: (ResponseDefinitionBuilder.() -> ResponseDefinitionBuilder) = { this }

    fun build(builder: WiremockStubBuilder.() -> Unit = {}) = this.apply(builder).buildStub()

    private fun buildStub(): StubMapping = mockComponent.wireMock.stubFor(
        WireMock.request(requestMethod.value(), WireMock.urlMatching(url))
            .runIfNotNull(jsonRequestBody) { withJsonRequestBody(it) }
            .runIfNotNull(token) {
                withHeader(HttpHeaders.AUTHORIZATION, WireMock.equalTo("Bearer $token"))
            }
            .requestBuilder()
            .willReturn(aJsonResponse(status, jsonResponseBody).responseBuilder())
    )
}

internal fun MockComponent.stub(
    requestMethod: RequestMethod,
    url: String,
    token: String? = null,
    builder: WiremockStubBuilder.() -> Unit = {}
) = WiremockStubBuilder(
    mockComponent = this,
    requestMethod = requestMethod,
    url = url,
    token = token
).build(builder)
