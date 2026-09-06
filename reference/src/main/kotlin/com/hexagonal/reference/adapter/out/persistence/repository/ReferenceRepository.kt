package com.hexagonal.reference.adapter.out.persistence.repository

import com.hexagonal.reference.domain.Reference
import org.springframework.data.jpa.repository.JpaRepository

interface ReferenceRepository : JpaRepository<Reference, Long> {

    fun findByIdAndDeletedAtIsNull(referenceId: Long): Reference?

}
