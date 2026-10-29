import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
}

dependencies {
    // Spring Data JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Spring Data Redis (Refresh Token 저장소)
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Kotlin Reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // MySQL Connector
    runtimeOnly("com.mysql:mysql-connector-j")

    // Spring Web MVC
    implementation("org.springframework.boot:spring-boot-starter-web")

    // 공통 모듈 참조 (security-common 이 Spring Security · OAuth2 Resource Server 를 함께 가져온다)
    implementation(project(":common"))
    implementation(project(":persistence-common"))
    implementation(project(":security-common"))
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = true
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
