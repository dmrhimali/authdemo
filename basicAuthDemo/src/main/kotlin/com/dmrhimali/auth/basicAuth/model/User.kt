package com.dmrhimali.auth.basicAuth.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val username: String,
    val password: String,
    val role: String
)
{
    // Default constructor required by Hibernate
    constructor() : this(0, "", "", "")
}