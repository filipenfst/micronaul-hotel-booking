package com.hotel.booking.application.gateway.httpclient.user.dto

import com.hotel.booking.domain.user.entities.User

data class UserResponse(
    val id: String,
    val name: String,
) {
    fun toDomain() = User(id = id, name = name)
}
