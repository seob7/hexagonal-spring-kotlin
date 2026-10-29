package com.hexagonal.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = ["com.hexagonal"])
class UserApplication

fun main(args: Array<String>) {
    runApplication<UserApplication>(*args)
}
