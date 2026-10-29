plugins {
    id("java-library")
}

dependencies {
    implementation(project(":common"))

    // Spring Security + JWT 검증(OAuth2 Resource Server). 사용하는 실행 모듈에 그대로 노출한다.
    api("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")

    // EntryPoint · ArgumentResolver 컴파일용. 런타임 Web MVC · Servlet 은 실행 모듈이 가져온다.
    compileOnly("org.springframework:spring-webmvc")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
