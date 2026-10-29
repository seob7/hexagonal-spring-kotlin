package com.hexagonal.security.config

import com.hexagonal.security.AuthUser
import com.hexagonal.security.resolver.AuthUserArgumentResolver
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class AuthUserWebConfig : WebMvcConfigurer {

    init {
        // AuthUser 는 요청 파라미터가 아니라 토큰에서 주입되므로 API 문서에서 숨긴다.
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(AuthUser::class.java)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(AuthUserArgumentResolver())
    }

}
