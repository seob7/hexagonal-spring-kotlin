package com.hexagonal.common.annotation

import org.springframework.stereotype.Repository

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Repository
annotation class PersistenceAdapter()
