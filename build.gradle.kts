val junitJupiterEngine: String by project
val jetbrainsExposedVersion: String by project
val koTestVersion: String by project
val kotlinLoggingVersion: String by project
val kotlinVersion: String by project
val kotlinxCoroutinesVersion: String by project
val logbackVersion: String by project
val micronautTestJunit5Version: String by project
val micronautVersion: String by project
val mockkVersion: String by project
val testContainersVersion: String by project
val mockServerClientVersion: String by project
val auth0jwtVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("kapt")
    jacoco
    id("com.github.ben-manes.versions")
    id("com.github.johnrengelman.shadow")
    id("io.micronaut.application")
    id("org.jetbrains.kotlin.plugin.allopen")
    id("org.jlleitschuh.gradle.ktlint")
}

version = "1.0.0"
group = "com.idiomcentric"

repositories {
    mavenCentral()
}

micronaut {
    version.set(micronautVersion)
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental.set(true)
        annotations.add("com.idiomcentric.*")
    }
}

allOpen {
    // Mark any classes with the following transactions as `open` automatically.
    annotations("io.micronaut.retry.annotation.Retryable")
}
dependencies {
    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.openapi:micronaut-openapi")
    kapt("io.micronaut.security:micronaut-security-annotations")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("com.auth0:jwks-rsa:$auth0jwtVersion")
    implementation("ch.qos.logback.contrib:logback-json-classic:$logbackVersion")
    implementation("ch.qos.logback.contrib:logback-jackson:$logbackVersion")
    implementation("ch.qos.logback:logback-classic")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("org.jetbrains.exposed:exposed-core:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$jetbrainsExposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$jetbrainsExposedVersion")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-graal")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut.cache:micronaut-cache-caffeine")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("javax.annotation:javax.annotation-api")
    implementation("org.graalvm.nativeimage:svm")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$kotlinxCoroutinesVersion")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("io.kotest:kotest-property:$koTestVersion")
    testImplementation("io.micronaut.test:micronaut-test-junit5:$micronautTestJunit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngine")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.testcontainers:mockserver:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
    testAnnotationProcessor("io.micronaut:micronaut-inject-java")
    testImplementation("org.mock-server:mockserver-client-java:$mockServerClientVersion")
}

application {
    mainClass.set("com.idiomcentric.Application")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec)
        this.languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(true)
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

nativeBuild {
    buildArgs.add("--initialize-at-run-time=org.postgresql.sspi.SSPIClient")
    buildArgs.add("--report-unsupported-elements-at-runtime")
    buildArgs.add("--initialize-at-build-time=org.postgresql.Driver,org.postgresql.util.SharedTimer")
}
