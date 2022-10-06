package com.alten.hotel.booking.application

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "Hotel Booking",
        version = "0.1",
        description = "Apis to manage a hotel reservations",
    )
)
object Application

fun main(vararg args: String) {
    Micronaut.run(Application::class.java, *args)
}
