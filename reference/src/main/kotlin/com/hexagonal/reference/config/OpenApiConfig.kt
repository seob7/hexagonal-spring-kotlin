package com.hexagonal.reference.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * OpenAPI(Swagger) 문서 메타데이터 설정.
 * 문서 UI 는 common 모듈에 적용된 springdoc-openapi-starter-webmvc-scalar 가 제공한다.
 * (JSON: /v3/api-docs, UI: /scalar)
 */
@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Drawly Reference API")
                    .description("Reference 모듈의 Input Adapter 가 제공하는 오픈 API 문서")
                    .version("v0.0.1")
            )
}
