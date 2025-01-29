package com.dmrhimali.auth.basicAuth.controller

import com.dmrhimali.auth.basicAuth.model.User
import com.dmrhimali.auth.basicAuth.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/admin")
class AdminController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    // Endpoint to get all users, only accessible to admins
    @PreAuthorize("hasRole('ADMIN')")  // Ensure that only users with 'ADMIN' role can access
    @GetMapping("/users")
    fun getAllUsers(): List<User> {
        // We are already using Spring Security's method-based security annotations (like @PreAuthorize("hasRole('ROLE_ADMIN')")),so we
        // don't need to manually verify if the user is authenticated here (see /api/sayhello implementation).
        // Spring Security will automatically check if the user is authenticated, and if they don't meet the authorization requirements (e.g., ROLE_ADMIN),
        // they will get a 403 Forbidden response.
        return userRepository.findAll()
    }

    // Endpoint to add a new admin user
    @PostMapping("/addAdmin")
    fun addAdmin(@RequestParam username: String, @RequestParam password: String): ResponseEntity<String> {
        // Check if the username already exists
        if (userRepository.findByUsername(username).isPresent) {
            return ResponseEntity.badRequest().body("Username already exists!")
        }

        // Encode the password before saving it
        val encodedPassword = passwordEncoder.encode(password)

        // Create a new User with ADMIN role
        val newAdmin = User(
            username = username,
            password = encodedPassword,
            role = "ADMIN"
        )

        // Save the new admin user
        userRepository.save(newAdmin)

        return ResponseEntity.ok("Admin user $username created successfully!")
    }
}