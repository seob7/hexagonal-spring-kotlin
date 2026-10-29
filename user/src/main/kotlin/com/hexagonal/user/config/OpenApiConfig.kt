package com.hexagonal.user.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * OpenAPI(Swagger) 문서 메타데이터 설정.
 * 문서 UI 는 common 모듈에 적용된 springdoc-openapi-starter-webmvc-scalar 가 제공한다.
 * (JSON: /v3/api-docs, UI: /scalar)
 *
 * `/api` 하위 어댑터는 `@SecurityRequirement(name = BEARER_SCHEME)` 를 붙여, 문서 UI 에서 Access Token 을 넣고 호출할 수 있게 한다.
 */
@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Hexagonal User API")
                    .description("User 모듈의 Input Adapter 가 제공하는 회원·인증 API 문서")
                    .version("v0.0.1")
            )
            .components(
                Components().addSecuritySchemes(
                    BEARER_SCHEME,
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            )

    companion object {
        const val BEARER_SCHEME = "bearer"
    }

}
