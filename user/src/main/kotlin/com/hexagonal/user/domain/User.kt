package com.hexagonal.user.domain

import com.hexagonal.persistence.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true, length = 100, comment = "이메일 (로그인 ID)")
    var email: String,

    @Column(nullable = false, length = 100, comment = "비밀번호 해시")
    var password: String,

    @Column(nullable = false, length = 20, comment = "닉네임")
    var nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, comment = "권한")
    var role: UserRole = UserRole.USER,
) : BaseEntity() {

    /** 비밀번호를 교체한다. 인코딩은 application 이 포트로 처리한 뒤 해시를 넘긴다. */
    fun changePassword(encodedPassword: String) {
        this.password = encodedPassword
    }

    /**
     * 회원 탈퇴. 이메일을 식별할 수 없는 값으로 바꾼 뒤 논리 삭제한다.
     *
     * 논리 삭제된 행도 email unique 제약에 포함되므로, 원래 이메일을 비워 둬야 같은 이메일로 재가입할 수 있다.
     * (MySQL 은 NULL 을 서로 다른 값으로 보기 때문에 unique(email, deleted_at) 로는 풀리지 않는다.)
     */
    fun withdraw() {
        this.email = "withdrawn-${requireNotNull(id)}@$WITHDRAWN_EMAIL_DOMAIN"
        softDelete()
    }

    companion object {
        // RFC 2606 예약 TLD. 실제 메일 주소와 절대 겹치지 않는다.
        private const val WITHDRAWN_EMAIL_DOMAIN = "withdrawn.invalid"
    }

}
