plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "com.study-kotlin"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.amazonaws:amazon-kinesis-client:1.15.2")
	implementation("software.amazon.kinesis:amazon-kinesis-client:3.0.1")
	implementation("software.amazon.awssdk:kinesis:2.29.43")
	implementation("software.amazon.awssdk:dynamodb:2.29.43")
	implementation("software.amazon.awssdk:cloudwatch:2.29.43")
	implementation("software.amazon.awssdk:auth:2.29.43")
	implementation("software.amazon.awssdk:core:2.29.43")
	implementation("software.amazon.awssdk:regions:2.29.43")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.slf4j:slf4j-api:2.1.0-alpha1")
	implementation("io.github.cdimascio:dotenv-java:3.1.0")
    runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("com.study-kotlin.backend.BackendApplication")
}

tasks.register<Copy>("getDependencies") {
    dependsOn("build")
    from(configurations.runtimeClasspath)
    into("runtime/")

    doFirst {
        val runtimeDir = File("runtime")
        runtimeDir.deleteRecursively()
        runtimeDir.mkdir()
    }

    doLast {
        println("Dependencies copied to runtime/")
    }
}
