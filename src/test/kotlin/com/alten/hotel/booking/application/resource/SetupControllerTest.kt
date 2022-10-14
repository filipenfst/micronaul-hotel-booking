package com.alten.hotel.booking.application.resource

import com.alten.hotel.booking.application.IntegrationTests
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import org.junit.jupiter.api.Test

internal class SetupControllerTest : IntegrationTests {
    @Inject
    private lateinit var spec: RequestSpecification
    @Test
    fun test(){
        spec.given().request()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
          .`when`().post("/setup-reservation?size=10&batchSize=5")
            .then()
            .statusCode(200)
    }
}