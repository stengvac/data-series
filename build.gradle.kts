import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"

    id("org.springframework.boot") version "2.6.2"

    application
}

group = "org.hw.data.series"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("org.hw.data.series.ApplicationKt")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    val springBootPlatform = platform(SpringBootPlugin.BOM_COORDINATES)
    annotationProcessor(springBootPlatform)

    implementation(springBootPlatform)
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2021.0.0"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation(platform("org.junit:junit-bom:5.8.0"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.platform:junit-platform-engine")
    testImplementation("org.assertj:assertj-core:3.22.0")

    testImplementation("io.mockk:mockk:1.12.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjsr305=warn")
        }
    }

    test {
        useJUnitPlatform {
            includeEngines("junit-jupiter")
            testLogging.exceptionFormat = TestExceptionFormat.FULL
            testLogging.showStandardStreams = true
        }
    }
}

repositories {
    mavenCentral()
}