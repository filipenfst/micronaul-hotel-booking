package com.hotel.booking.application.gateway.httpclient.user

import com.hotel.booking.application.gateway.httpclient.config.ClientConfiguration
import com.hotel.booking.application.gateway.httpclient.user.dto.UserResponse
import com.hotel.booking.domain.user.entities.User
import com.hotel.booking.domain.user.services.UserPort
import io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import jakarta.inject.Named
import jakarta.inject.Singleton
import kotlinx.coroutines.reactive.awaitSingle


@Singleton
class UserHttpClientGateway(
    @Named("user-api") private val clientConfiguration: ClientConfiguration,
) : UserPort {
    private val httpClient = clientConfiguration.httpClient()

    override suspend fun findUser(id: String): User? = clientConfiguration.circuitBreaker.executeSuspendFunction {
        httpClient.awaitExchange<UserResponse?>(
            request = HttpRequest.GET("/user/$id")
        ) {
            if (status == HttpStatus.OK) {
                body()
            } else {
                TODO()
            }
        }
    }?.toDomain()
}

suspend inline fun <reified T> HttpClient.awaitExchange(
    request: HttpRequest<Any>,
    responseHandler: HttpResponse<T>.() -> T
): T = exchange(request, T::class.java).awaitSingle().responseHandler()


