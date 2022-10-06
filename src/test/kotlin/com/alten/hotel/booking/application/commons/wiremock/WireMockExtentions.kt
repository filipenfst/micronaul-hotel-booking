package com.alten.hotel.booking.application.commons.wiremock

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder

internal fun <E : Any, A : Any> E.runIfNotNull(arg: A?, function: (E.(A) -> E)) =
    if (arg == null) this else this.function(arg)

internal fun MappingBuilder.withJsonRequestBody(
    requestBodyBuilder: (() -> String)
): MappingBuilder = withRequestBody(WireMock.equalToJson(requestBodyBuilder(), true, false))


internal fun aJsonResponse(
    status: Int = 200,
    jsonResponseBody: (() -> String)? = null
): ResponseDefinitionBuilder = WireMock.aResponse().withStatus(status)
    .runIfNotNull(jsonResponseBody) { withJsonResponseBody(it) }


internal fun ResponseDefinitionBuilder.withJsonResponseBody(
    jsonResponseBody: (() -> String)
): ResponseDefinitionBuilder = withHeader("Content-Type", "application/json")
    .withBody(jsonResponseBody())


fun RequestPatternBuilder.withJsonRequestBody(
    ignoreArrayOrder: Boolean = false,
    ignoreExtraElements: Boolean = false,
    requestBodyBuilder: (() -> String)
): RequestPatternBuilder = withRequestBody(
    WireMock.equalToJson(
        requestBodyBuilder(),
        ignoreArrayOrder,
        ignoreExtraElements
    )
)
