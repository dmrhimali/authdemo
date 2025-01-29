package com.dmrhimali.auth.jwtauth.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority

@Entity
@Table(name = "roles")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String  // Role name, e.g., "ROLE_USER", "ROLE_ADMIN"
) : GrantedAuthority {

    // Default constructor required by Hibernate
    constructor() : this(0, "")

    @JsonValue
    override fun getAuthority(): String {
        return name
    }

    // Provide a static factory method to deserialize the string into a Role object
    companion object {
        @JsonCreator
        fun fromString(name: String): Role {
            return Role(name = name)
        }
    }
}
