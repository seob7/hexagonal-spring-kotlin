package com.hexagonal.reference.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * QueryDSL 쿼리 생성기 빈 등록.
 * `EntityManager` 는 실행 애플리케이션(reference)의 영속성 컨텍스트를 사용해야 하므로
 * persistence-common 이 아닌 여기(reference)에 둔다.
 */
@Configuration
class QuerydslConfig(
    @PersistenceContext private val entityManager: EntityManager,
) {

    @Bean
    fun jpaQueryFactory(): JPAQueryFactory =
        JPAQueryFactory(entityManager)

}