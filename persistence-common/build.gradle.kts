plugins {
    id("java-library")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
    kotlin("kapt")
}

val hypersistenceUtilsVersion: String by rootProject.extra
val querydslVersion: String by rootProject.extra

dependencies {
    // BaseEntity annotations and TSID identifier generator
    api("jakarta.persistence:jakarta.persistence-api")
    api("org.springframework.data:spring-data-jpa")
    api("io.hypersistence:hypersistence-utils-hibernate-70:$hypersistenceUtilsVersion")

    // QueryDSL (Jakarta 지원 OpenFeign 포크)
    api("io.github.openfeign.querydsl:querydsl-jpa:$querydslVersion")

    // QueryDSL Q클래스 생성 (Kotlin 은 kapt 로 어노테이션 프로세서를 태운다)
    kapt("io.github.openfeign.querydsl:querydsl-apt:$querydslVersion:jpa")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
