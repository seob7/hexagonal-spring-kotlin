package com.hexagonal.reference

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = ["com.hexagonal"])
class ReferenceApplication

fun main(args: Array<String>) {
    runApplication<ReferenceApplication>(*args)
}
