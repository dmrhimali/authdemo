package com.dmrhimali.auth.jwtauth.repository

import com.dmrhimali.auth.jwtauth.model.Role
import com.dmrhimali.auth.jwtauth.model.User
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class UserRoleInitializer(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val roleRepository: RoleRepository,
    @Autowired private val passwordEncoder: PasswordEncoder
) {

    @PostConstruct
    fun init() {
        // Ensure roles :user and admin exist
        val userRole = roleRepository.save(Role(name = "ROLE_USER"))
        val adminRole = roleRepository.save(Role(name = "ROLE_ADMIN"))

        // Ensure the admin user exists
        var adminUser = userRepository.findByUsername("admin").getOrNull()
        if (adminUser == null) {
                adminUser = User(
                    username = "admin",
                    password = passwordEncoder.encode("adminpassword"), // encode the password
                    roles = mutableSetOf(adminRole)
                )
            userRepository.save(adminUser)
        }

        // Ensure a regular user exists
        var regularUser = userRepository.findByUsername("user").getOrNull()
        if (regularUser == null) {
            regularUser = User(
                username = "user",
                password = passwordEncoder.encode("userpassword"), // encode the password
                roles = mutableSetOf(userRole)
            )
            userRepository.save(regularUser)
        }

        adminUser = userRepository.findByUsername("admin").getOrNull()
        if (adminUser != null && adminUser.roles.none { it.name == "ROLE_ADMIN" }) {
            adminUser.roles = mutableSetOf(adminRole)
            userRepository.save(adminUser)
        }

        regularUser = userRepository.findByUsername("user").getOrNull()
        if (regularUser != null && regularUser.roles.none { it.name == "ROLE_USER" }) {
            regularUser.roles = mutableSetOf(userRole)
            userRepository.save(regularUser)
        }
    }
}