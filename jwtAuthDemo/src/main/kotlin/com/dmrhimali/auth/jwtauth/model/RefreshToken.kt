package com.dmrhimali.auth.jwtauth.model

import jakarta.persistence.*
import java.util.*

//We will store refresh tokens in the database (optional, but recommended for security reasons), or generate them dynamically and send them to the client.
@Entity
@Table(name = "refresh_tokens")
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val token: String, // The refresh token value
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User, // Link to the user entity
    val expirationDate: Date
)