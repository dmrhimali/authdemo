package com.dmrhimali.auth.noauth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
open class SecurityConfig {

    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeRequests()
            .anyRequest().permitAll()  // Allow all requests (no authentication)
            .and()
            .csrf().disable()  // Disable CSRF protection if not needed
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Optional: make app stateless (no session)
        return http.build()
    }
}