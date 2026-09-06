import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    // Kotlin
    kotlin("jvm") version "2.3.21" apply false
    kotlin("plugin.spring") version "2.3.21" apply false
    kotlin("plugin.jpa") version "2.3.21" apply false
    kotlin("plugin.allopen") version "2.3.21" apply false
    kotlin("kapt") version "2.3.21" apply false

    // Spring Boot
    id("org.springframework.boot") version "4.1.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "com.hexagonal"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

// 버전 전역 관리
extra["commonApiVersion"] = "0.0.2"
extra["hypersistenceUtilsVersion"] = "3.15.3"
extra["swaggerVersion"] = "3.1.0"
extra["querydslVersion"] = "7.6"

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    // Spring Boot
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    dependencies {
        "implementation"("org.springframework.boot:spring-boot-starter-validation")

        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    // 기본적으로 모든 BootJar 는 False 로 설정하고, 각 모듈에서 True 로 설정한다.
    tasks.withType<BootJar> {
        enabled = false
    }

    tasks.withType<Jar> {
        enabled = true
    }
}
