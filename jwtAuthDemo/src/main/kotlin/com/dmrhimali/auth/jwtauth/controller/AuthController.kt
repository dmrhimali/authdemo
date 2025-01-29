package com.dmrhimali.auth.jwtauth.controller


import com.dmrhimali.auth.jwtauth.config.JwtUtil
import com.dmrhimali.auth.jwtauth.model.User
import com.dmrhimali.auth.jwtauth.repository.RoleRepository
import com.dmrhimali.auth.jwtauth.repository.UserRepository
import jakarta.persistence.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*

data class LoginRequest(val username: String, val password: String){
    constructor() : this("", "") // Add a default constructor to allow Jackson to instantiate the LoginRequest even if it's empty.
}

@RestController
@RequestMapping("/api")
class AuthController(
    @Autowired val authenticationManager: AuthenticationManager,
    @Autowired val jwtUtil: JwtUtil,
    @Autowired val userRepository: UserRepository,
    @Autowired private val roleRepository: RoleRepository,
    @Autowired private val passwordEncoder: PasswordEncoder
) {
    @PostMapping("/register")
    fun register(@RequestBody user: User): ResponseEntity<String> {
        // Check for existing user
        val existingUser = userRepository.findByUsername(user.username)?.orElse(null)
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists!")
        }

        // Retrieve existing roles based on names (using a more efficient approach)
        val roleNames = user.roles.map { it.name }.toList()
        val existingRoles = roleRepository.findByNameIn(roleNames) // Find roles by names in a single query

        // Handle missing roles (optional, based on your requirements)
        val nonExistingRoleNames = roleNames.minus(existingRoles.map { it.name }.toSet())
        if (nonExistingRoleNames.isNotEmpty()) {
            val errorMessage = "Roles '${nonExistingRoleNames.joinToString(", ")}' not found in the database."
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage)
        }

        // Create new user with existing roles (using merge)
        val newUser = User (
            username = user.username,
            password = passwordEncoder.encode(user.password), // Encode the password
            roles = existingRoles.toSet() //assign existing looked up roles so new roles are not saved in roles table.
        )

        // Merge the user with existing roles (ensures JPA manages relationships)
        val savedUser = userRepository.saveAndFlush(newUser) // Persist user and associated roles

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!")
    }

    @PostMapping("/login")
    fun login(@RequestBody credentials: LoginRequest): ResponseEntity<Map<String, String>> {
        val username = credentials.username
        val password = credentials.password
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(username, password)
        )

        SecurityContextHolder.getContext().authentication = authentication
        val token = jwtUtil.generateToken(username, authentication.authorities.map { it.authority }.toList())

        return ResponseEntity.ok(mapOf("token" to token))
    }

    @PostMapping("/refresh")
    fun refresh(@RequestHeader("Authorization") token: String): ResponseEntity<Map<String, String>> {
        val newToken = jwtUtil.generateToken(jwtUtil.extractUsername(token), jwtUtil.extractRoles(token))
        return ResponseEntity.ok(mapOf("token" to newToken))
    }

    @GetMapping("/greet")
    fun greet(): ResponseEntity<String> {
        val username = SecurityContextHolder.getContext().authentication.name
        return ResponseEntity.ok("Hello, $username!")
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    fun getUsers(): ResponseEntity<List<User>> {
        val users = userRepository.findAll()
        return ResponseEntity.ok(users)
    }
}