package com.hexagonal.reference.adapter.out.persistence.repository

import com.hexagonal.reference.domain.QReference.reference
import com.hexagonal.reference.domain.Reference
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

/**
 * QueryDSL 기반 복잡 조회를 전담하는 Repository.
 * 단순 CRUD 는 [ReferenceRepository] 를, 동적/복합 조건 조회는 여기를 사용한다.
 */
@Repository
class ReferenceQueryRepository(
    private val queryFactory: JPAQueryFactory,
) {

    fun findReferences(pageable: Pageable): Page<Reference> {
        val content = queryFactory
            .selectFrom(reference)
            .where(reference.deletedAt.isNull)
            .orderBy(reference.id.desc()) // TSID 는 생성 순서를 보장하므로 id 내림차순 = 최신순
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = queryFactory
            .select(reference.count())
            .from(reference)
            .where(reference.deletedAt.isNull)

        // content 가 페이지 크기보다 작으면(마지막 페이지) count 쿼리를 생략한다.
        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

}