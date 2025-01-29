package com.dmrhimali.auth.noauth

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GreetingController(private val apiService: ApiService) {

    @GetMapping("/")
    fun home() = apiService.home()

    @GetMapping("/greet")
    fun greet() = apiService.getGreeting()
}