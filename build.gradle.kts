import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
}

group = "wisoft.io"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("de.svenkubiak:jBCrypt:0.4.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.linecorp.kotlin-jdsl:spring-data-kotlin-jdsl-starter-jakarta:2.2.1.RELEASE")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.1")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.7.3")
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("org.flywaydb:flyway-core")

    implementation("com.amazonaws:aws-java-sdk-s3:1.12.174")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.8.1")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<BootJar> {
    archiveFileName.set("api-server.jar")
    val jarFile = "build/libs/api-server.jar"
    val destinationDir = file("docker/server")

    doLast {
        copy {
            from(jarFile)
            into(destinationDir)
        }
    }
}

val installLocalGitHook =
    tasks.register<Copy>("installLocalGitHook") {
        from("${rootProject.rootDir}/.github/hooks")
        into(File("${rootProject.rootDir}/.git/hooks"))

        eachFile {
            mode = "755".toInt(radix = 8)
        }
    }

tasks.build {
    dependsOn(installLocalGitHook)
}

tasks.bootJar {
    dependsOn(installLocalGitHook)
}
