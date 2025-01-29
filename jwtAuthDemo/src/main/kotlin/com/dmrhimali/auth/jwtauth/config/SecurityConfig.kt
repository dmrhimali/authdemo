package com.dmrhimali.auth.jwtauth.config

import com.dmrhimali.auth.jwtauth.service.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
open class SecurityConfig(
    private val jwtUtil: JwtUtil,
    private val customUserDetailsService: CustomUserDetailsService
) {

    // Define SecurityFilterChain to handle URL-based security configuration
    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { authz ->
                authz
                    // Allow unauthenticated access for registration, login, and refresh endpoints
                    .requestMatchers("/api/register", "/api/login", "/api/refresh").permitAll()
                    // Protect /api/users endpoint - Only accessible to users with ADMIN role
                    .requestMatchers("/api/users").hasRole("ADMIN")
                    // Protect /api/greet endpoint - Accessible to authenticated users, regardless of role
                    .requestMatchers("/api/greet").authenticated()
                    // Any other request should be authenticated
                    .anyRequest().authenticated()
            }
            .addFilterBefore(JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter::class.java) // Add JWT filter before the default UsernamePasswordAuthenticationFilter. Should  intercept only authenticated requests
        return http.build() // Return the SecurityFilterChain
    }

    @Bean
    open fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    // Define the AuthenticationManager Bean using DaoAuthenticationProvider for /login
    @Bean
    open fun authenticationManager(
        http: HttpSecurity,
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder
    ): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)

        // Configure the DaoAuthenticationProvider for username/password authentication
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService)
        authenticationProvider.setPasswordEncoder(passwordEncoder)

        authenticationManagerBuilder.authenticationProvider(authenticationProvider)

        return authenticationManagerBuilder.build()
    }


    @Bean
    open fun userDetailsService(): UserDetailsService {
        return customUserDetailsService // Or CustomUserDetailsService if needed
    }

}
