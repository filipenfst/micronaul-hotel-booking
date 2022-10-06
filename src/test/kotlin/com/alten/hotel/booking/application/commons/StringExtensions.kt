package com.alten.hotel.booking.application.commons

import wiremock.com.jayway.jsonpath.DocumentContext
import wiremock.com.jayway.jsonpath.JsonPath

fun String.payload() = {}.javaClass.getResource("/payloads/$this")!!.readText()

inline fun String.payload(builder: DocumentContext.() -> Unit): String = JsonPath.parse(payload()).also {
    it.builder()
}.jsonString()

