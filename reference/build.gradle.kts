import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
    kotlin("kapt")
}

val querydslVersion: String by rootProject.extra

dependencies {
    // Spring Data JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Kotlin Reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // MySQL Connector
    runtimeOnly("com.mysql:mysql-connector-j")

    // Spring Web MVC
    implementation("org.springframework.boot:spring-boot-starter-web")

    // 공통 모듈 참조
    implementation(project(":common"))
    implementation(project(":persistence-common"))

    // QueryDSL Q클래스 생성 (도메인 엔티티가 이 모듈에 있으므로 kapt 를 여기서도 태운다)
    kapt("io.github.openfeign.querydsl:querydsl-apt:$querydslVersion:jpa")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")
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
