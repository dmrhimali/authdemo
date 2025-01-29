package com.dmrhimali.auth.noauth

import org.springframework.stereotype.Service

interface ApiService {
    fun getGreeting(): String
    fun home(): String
}

@Service
class ApiServiceImpl : ApiService {
    override fun getGreeting(): String {
        return "Hello from Service Module!"
    }

    override fun home(): String {
        return "Home!"
    }
}