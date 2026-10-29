package com.hexagonal.user.application.port.out

import com.hexagonal.user.domain.User

interface UserPersistencePort {

    fun create(user: User): User

    fun findUser(userId: Long): User

    /** 로그인용 조회. 없을 수 있으므로 nullable 이다. (없음 → 404 가 아니라 401 로 응답해야 한다) */
    fun getUserByEmail(email: String): User?

    /** 탈퇴 회원을 포함한 이메일 중복 여부. DB 의 unique 제약과 같은 범위를 본다. */
    fun existsByEmail(email: String): Boolean

}
