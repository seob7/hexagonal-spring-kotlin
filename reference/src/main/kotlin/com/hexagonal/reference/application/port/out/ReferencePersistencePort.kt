package com.hexagonal.reference.application.port.out

import com.hexagonal.reference.domain.Reference
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReferencePersistencePort {

    fun create(reference: Reference): Reference

    fun findReferences(pageable: Pageable): Page<Reference>

    fun findReference(referenceId: Long): Reference

}
