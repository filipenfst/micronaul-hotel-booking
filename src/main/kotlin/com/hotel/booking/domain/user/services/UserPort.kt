package com.hotel.booking.domain.user.services

import com.hotel.booking.domain.user.entities.User

interface UserPort {
    suspend fun findUser(id: String):User
}
