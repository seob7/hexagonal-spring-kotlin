package com.hexagonal.common.annotation

import org.springframework.stereotype.Component

/**
 * 웹·영속성이 아닌 아웃바운드 어댑터. (비밀번호 암호화, 토큰 발급 등)
 * 외부 API 호출은 [WebOutputAdapter], DB·캐시 저장소는 [PersistenceAdapter] 를 쓴다.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class OutputAdapter()
