package com.dmrhimali.auth.jwtauth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.dmrhimali.auth.jwtauth"])
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}