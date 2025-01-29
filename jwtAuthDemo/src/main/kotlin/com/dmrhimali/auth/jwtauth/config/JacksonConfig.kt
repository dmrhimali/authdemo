package com.dmrhimali.auth.jwtauth.config

import com.dmrhimali.auth.jwtauth.model.Role
import com.dmrhimali.auth.jwtauth.model.RoleDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class JacksonConfig {

    @Bean
    open fun objectMapper(): ObjectMapper {
        val module = SimpleModule()
        module.addDeserializer(Role::class.java, RoleDeserializer())  // Register custom deserializer for Role

        return ObjectMapper().apply {
            registerModule(module)
        }
    }
}
