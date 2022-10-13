import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.micronaut.application") version "3.6.2"
    id("io.gitlab.arturbosch.detekt") version "1.21.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("kapt") version "1.7.10"
    kotlin("jvm") version "1.7.10"
    jacoco
}

group = "com.alten.hotel.booking"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

val testcontainersVersion = "1.17.4"
val resilience4jVersion = "1.7.1"
val coroutinesVersion = "1.6.4"
val jacksonVersion = "2.13.4"
val wiremockVersion = "2.34.0"
val mockkVersion = "1.13.2"
val postgresqlVersion = "42.5.0"
val ktlint by configurations.creating

dependencies {
    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("io.micronaut.openapi:micronaut-openapi")
    kapt("io.micronaut:micronaut-management")
    kapt("io.micronaut:micronaut-graal")
    kapt("io.micronaut.serde:micronaut-serde-processor")

    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("io.micronaut.data:micronaut-data-r2dbc")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.serde:micronaut-serde-jackson")

    implementation("io.micronaut.tracing:micronaut-tracing-zipkin")

    implementation("io.github.resilience4j:resilience4j-kotlin:$resilience4jVersion")
    implementation("io.github.resilience4j:resilience4j-retry:$resilience4jVersion")
    implementation("jakarta.annotation:jakarta.annotation-api")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")
    implementation("io.netty:netty-handler:4.1.82.Final")
    implementation("ch.qos.logback:logback-classic:1.4.3")
    implementation("org.slf4j:slf4j-simple")
    implementation("org.slf4j:slf4j-api")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("io.micronaut:micronaut-management:3.7.1")

    compileOnly("org.graalvm.nativeimage:svm")

    runtimeOnly("io.r2dbc:r2dbc-postgresql:0.8.13.RELEASE")
    runtimeOnly("org.postgresql:postgresql:42.5.0")

    ktlint("com.pinterest:ktlint:0.47.1")

    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("io.micronaut.test:micronaut-test-rest-assured")
    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:$wiremockVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
}

configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("io.micronaut:micronaut-jackson-databind"))
            .using(module("io.micronaut.serde:micronaut-serde-jackson:1.3.2"))
    }
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.alten.hotel.booking.application.*")
    }
}
application {
    mainClass.set("com.alten.hotel.booking.application.ApplicationKt")
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
}

val outputDir = "${project.buildDir}/reports/ktlint/"
val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))



tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

val ktlintFormat by tasks.creating(JavaExec::class) {
    println("Executing ktlintFormat")
    inputs.files(inputFiles)
    outputs.dir(outputDir)

    description = "Fix Kotlin code style deviations."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("-F", "src/**/*.kt")

    jvmArgs = listOf("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

val ktlintCheck by tasks.creating(JavaExec::class) {
    println("Executing ktlintCheck")
    inputs.files(inputFiles)
    outputs.dir(outputDir)

    description = "Check Kotlin code style."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("src/**/*.kt")
}

val exclusion: Array<String> = arrayOf(
    "com/alten/hotel/booking/commons/**",
    "com/alten/hotel/booking/application/ApplicationKt.*",
)
tasks.withType<JacocoReport> {
    dependsOn(tasks.test)
    doLast {
        println("View code coverage at:")
        println("file://$buildDir/reports/jacoco/test/html/index.html")
    }
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it).apply {
                exclude(*exclusion)
            }
        }))
    }
}
tasks.withType<JacocoCoverageVerification> {
    dependsOn(tasks.jacocoTestReport)
//    violationRules {
//        isFailOnViolation = true
//        rule {
//            limit {
//                counter = "INSTRUCTION"
//                minimum = "0.99".toBigDecimal()
//            }
//        }
//        rule {
//            limit {
//                counter = "LINE"
//                minimum = "0.99".toBigDecimal()
//            }
//        }
//        rule {
//            limit {
//                counter = "BRANCH"
//                minimum = "1.00".toBigDecimal()
//            }
//        }
//        rule {
//            limit {
//                counter = "METHOD"
//                minimum = "1.00".toBigDecimal()
//            }
//        }
//        rule {
//            limit {
//                counter = "CLASS"
//                minimum = "1.00".toBigDecimal()
//            }
//        }
//    }
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it).apply {
                exclude(*exclusion)
            }
        }))
    }
}

tasks {
    graalvmNative {
        binaries {
            named("main") {
                imageName.set("hotel-booking")
                buildArgs.add("--verbose")
                buildArgs.add("--initialize-at-run-time=com.fasterxml.jackson.module.kotlin")
            }
        }
    }
}