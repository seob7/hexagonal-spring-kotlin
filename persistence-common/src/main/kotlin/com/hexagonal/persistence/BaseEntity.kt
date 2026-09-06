package com.hexagonal.persistence

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * 모든 JPA Entity가 상속하는 공통 영속성 메타데이터.
 *
 * - 식별자는 DB auto increment가 아닌 TSID 로 애플리케이션에서 생성한다.
 * - 생성·수정 시각은 JPA Auditing으로 관리하며, 실행 애플리케이션에
 *   `@EnableJpaAuditing` 설정이 반드시 필요하다.
 * - deletedAt은 논리 삭제 시각이다.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    @Id
    @Tsid
    var id: Long? = null
        protected set

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null
        protected set

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null
        protected set

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
        protected set

    fun softDelete() {
        deletedAt = LocalDateTime.now()
    }

}