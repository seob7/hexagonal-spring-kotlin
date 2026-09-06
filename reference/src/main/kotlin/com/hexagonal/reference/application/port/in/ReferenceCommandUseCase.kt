package com.hexagonal.reference.application.port.`in`

import com.hexagonal.reference.application.port.`in`.dto.CreateReferenceCommand
import com.hexagonal.reference.application.port.`in`.dto.UpdateReferenceCommand
import com.hexagonal.reference.domain.Reference

interface ReferenceCommandUseCase {

    fun createReference(command: CreateReferenceCommand): Reference

    fun updateReference(referenceId: Long, command: UpdateReferenceCommand): Reference

    fun deleteReference(referenceId: Long)

}
