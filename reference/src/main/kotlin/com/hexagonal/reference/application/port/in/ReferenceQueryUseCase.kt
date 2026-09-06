package com.hexagonal.reference.application.port.`in`

import com.hexagonal.reference.domain.Reference
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReferenceQueryUseCase {

    fun findReferences(pageable: Pageable): Page<Reference>

    fun findReference(referenceId: Long): Reference

}
