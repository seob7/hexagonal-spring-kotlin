package com.hexagonal.reference.adapter.out.persistence

import com.hexagonal.common.annotation.PersistenceAdapter
import com.hexagonal.common.exception.NotFoundException
import com.hexagonal.common.exception.errorcode.NotFoundErrorCode
import com.hexagonal.reference.adapter.out.persistence.repository.ReferenceQueryRepository
import com.hexagonal.reference.adapter.out.persistence.repository.ReferenceRepository
import com.hexagonal.reference.application.port.out.ReferencePersistencePort
import com.hexagonal.reference.domain.Reference
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

@PersistenceAdapter
class ReferencePersistenceAdapter(
    private val referenceRepository: ReferenceRepository,
    private val referenceQueryRepository: ReferenceQueryRepository,
) : ReferencePersistencePort {

    override fun create(reference: Reference): Reference = referenceRepository.save(reference)

    override fun findReferences(pageable: Pageable): Page<Reference> =
        referenceQueryRepository.findReferences(pageable)

    override fun findReference(referenceId: Long): Reference =
        referenceRepository.findByIdAndDeletedAtIsNull(referenceId)
            ?: throw NotFoundException(NotFoundErrorCode.REFERENCE_NOT_FOUND)

}
