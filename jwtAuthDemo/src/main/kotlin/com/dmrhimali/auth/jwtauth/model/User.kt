package com.dmrhimali.auth.jwtauth.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "username") // Make sure this is correctly mapped to the "username" column
    private val username: String,  // Private backing field for username

    @Column(name = "password")
    private var password: String,  // Private backing field for password

    @ManyToMany(fetch = FetchType.EAGER) // Fetch roles eagerly so they are available for authorization
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: Set<Role> = emptySet()
) : UserDetails {

    // Default constructor required by Hibernate
    constructor() : this(0, "", "", emptySet())

    override fun getAuthorities(): Collection<GrantedAuthority> =
        roles.map { role -> org.springframework.security.core.authority.SimpleGrantedAuthority(role.name) }

    override fun getPassword(): String = password  // Return the private _password field

    override fun getUsername(): String = username  // Return the private _username field

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

}