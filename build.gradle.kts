val junitJupiterEngine: String by project
val kotlinLoggingVersion: String by project
val kotlinVersion: String by project
val kotlinxCoroutinesVersion: String by project
val logbackVersion: String by project
val micronautTestJunit5: String by project
val micronautVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.github.ben-manes.versions")
    id("com.github.johnrengelman.shadow")
    id("io.micronaut.application")
    id("org.jetbrains.kotlin.plugin.allopen")
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
    implementation("ch.qos.logback.contrib:logback-json-classic:$logbackVersion")
    implementation("ch.qos.logback.contrib:logback-jackson:$logbackVersion")
    implementation("ch.qos.logback:logback-classic")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-graal")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut.cache:micronaut-cache-caffeine")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("javax.annotation:javax.annotation-api")
    implementation("org.graalvm.nativeimage:svm")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$kotlinxCoroutinesVersion")

    testImplementation("io.micronaut.test:micronaut-test-junit5:$micronautTestJunit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngine")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
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
