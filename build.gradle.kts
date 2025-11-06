plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"
java.sourceCompatibility = JavaVersion.VERSION_17

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // --- Spring Boot base --- //
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // --- Base de datos reactiva con PostgreSQL --- //
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.r2dbc:r2dbc-postgresql:0.8.13.RELEASE")
    implementation("io.r2dbc:r2dbc-pool")

    // --- Serialización y reflexión --- //
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // --- Kafka (opcional, según tu arquitectura) --- //
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.apache.kafka:kafka-streams")

    // --- Swagger --- //
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")


    // --- Soporte adicional para tests y lombok --- //
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootBuildImage {
    runImage = "paketobuildpacks/ubuntu-noble-run-base:latest"
}

springBoot {
    mainClass.set("com.example.chat.AppKt")
}

tasks.bootRun {
    mainClass.set("com.example.chat.AppKt")
}

tasks.bootJar {
    mainClass.set("com.example.chat.AppKt")
}
