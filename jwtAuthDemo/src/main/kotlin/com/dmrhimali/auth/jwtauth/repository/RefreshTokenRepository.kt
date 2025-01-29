package com.dmrhimali.auth.jwtauth.repository

import com.dmrhimali.auth.jwtauth.model.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): Optional<RefreshToken>
    fun deleteByUserId(userId: Long): Int
}
