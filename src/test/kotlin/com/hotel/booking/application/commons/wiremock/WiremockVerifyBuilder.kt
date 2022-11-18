package com.hotel.booking.application.commons.wiremock

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import wiremock.com.google.common.net.HttpHeaders

internal open class WiremockVerifyBuilder(
    private val mockComponent: MockComponent,
    private val requestMethod: RequestMethod,
    private val url: String
) {
    var count: Int = 1
    var jsonRequestBody: (() -> String)? = null
    var requestBuilder: (RequestPatternBuilder.() -> RequestPatternBuilder) = { this }
    var token: String? = null

    fun verify(builder: WiremockVerifyBuilder.() -> Unit = {}) = this.apply(builder).buildVerify()

    private fun buildVerify() = mockComponent.wireMock.verify(
        count,
        RequestPatternBuilder(requestMethod, urlMatching(url))
            .runIfNotNull(jsonRequestBody) {
                withJsonRequestBody(
                    ignoreExtraElements = true,
                    ignoreArrayOrder = true,
                    requestBodyBuilder = it
                )
            }
            .runIfNotNull(token) {
                withHeader(HttpHeaders.AUTHORIZATION, WireMock.equalTo("Bearer $token"))
            }
            .requestBuilder()
    )
}

internal fun MockComponent.verify(
    requestMethod: RequestMethod,
    url: String,
    builder: WiremockVerifyBuilder.() -> Unit = {}
) = WiremockVerifyBuilder(
    mockComponent = this,
    requestMethod = requestMethod,
    url = url,
).verify(builder)
