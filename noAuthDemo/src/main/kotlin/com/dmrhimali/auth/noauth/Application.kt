package com.dmrhimali.auth.noauth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.dmrhimali.auth.noauth"])
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}