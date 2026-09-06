package com.hexagonal.common.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@RestController
@RequestMapping
annotation class WebInputAdapter(
    @get:AliasFor(annotation = RequestMapping::class, attribute = "value")
    vararg val value: String,
)
