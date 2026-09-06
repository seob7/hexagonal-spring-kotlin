package com.hexagonal.reference.application.service

import com.hexagonal.reference.application.port.`in`.ReferenceCommandUseCase
import com.hexagonal.reference.application.port.`in`.ReferenceQueryUseCase
import com.hexagonal.reference.application.port.`in`.dto.CreateReferenceCommand
import com.hexagonal.reference.application.port.`in`.dto.UpdateReferenceCommand
import com.hexagonal.reference.application.port.out.ReferencePersistencePort
import com.hexagonal.reference.domain.Reference
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ReferenceService(
    private val referencePersistencePort: ReferencePersistencePort,
) : ReferenceCommandUseCase, ReferenceQueryUseCase {

    @Transactional
    override fun createReference(command: CreateReferenceCommand): Reference =
        referencePersistencePort.create(
            Reference(
                title = command.title,
                content = command.content,
            )
        )

    override fun findReferences(pageable: Pageable): Page<Reference> =
        referencePersistencePort.findReferences(pageable)

    override fun findReference(referenceId: Long): Reference =
        referencePersistencePort.findReference(referenceId)

    @Transactional
    override fun updateReference(referenceId: Long, command: UpdateReferenceCommand): Reference =
        referencePersistencePort.findReference(referenceId).apply {
            update(command.title, command.content)
        }

    @Transactional
    override fun deleteReference(referenceId: Long) {
        referencePersistencePort.findReference(referenceId).softDelete()
    }

}
