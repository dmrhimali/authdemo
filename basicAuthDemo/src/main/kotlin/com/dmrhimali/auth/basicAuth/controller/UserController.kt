package com.dmrhimali.auth.basicAuth.controller

import com.dmrhimali.auth.basicAuth.model.User
import com.dmrhimali.auth.basicAuth.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class UserController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    // Registration Endpoint
    @PostMapping("/register")
    fun register(@RequestParam username: String, @RequestParam password: String): String {
        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent) {
            return "Username already exists!"
        }

        // Encode the password before saving
        val encodedPassword = passwordEncoder.encode(password)

        val newUser = User(
            username = username,
            password = encodedPassword,
            role = "USER")

        userRepository.save(newUser)
        return "User registered successfully"
    }

    // Hello Endpoint (only accessible to authenticated users)
    @GetMapping("/sayhello")
    fun sayHello(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated) {
            return "You are not authenticated!"
        }
        return "Hello, ${authentication.name}!"
    }

}
