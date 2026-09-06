plugins {
    id("java-library")
}

val commonApiVersion: String by rootProject.extra
val swaggerVersion: String by rootProject.extra

dependencies {
    // Lightweight and reusable API response & exception handling utils
    api("io.github.seob7:common-api:${commonApiVersion}")

    compileOnly("org.springframework:spring-web")

    // Springdoc OpenAPI for generating API documentation Scalar Version
    api("org.springdoc:springdoc-openapi-starter-webmvc-scalar:${swaggerVersion}")

}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
