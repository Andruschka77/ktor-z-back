val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val commons_codec_version: String by project
val postgresql_version: String by project
val exposed_version: String by project
val hikari_version: String by project

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
//    implementation(libs.ktor.server.auth)
//    implementation(libs.ktor.server.core)
//    implementation(libs.ktor.server.auth.jwt)
//    implementation(libs.ktor.server.call.logging)
//    implementation(libs.ktor.serialization.kotlinx.json)
//    implementation(libs.ktor.server.content.negotiation)
//    implementation(libs.exposed.core)
//    implementation(libs.exposed.jdbc)
//    implementation(libs.h2)
//    implementation(libs.ktor.server.netty)
//    implementation(libs.logback.classic)
//    implementation(libs.ktor.server.config.yaml)
//    testImplementation(libs.ktor.server.test.host)
//    testImplementation(libs.kotlin.test.junit)
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-content-negotiation:3.1.1")
    implementation("io.ktor:ktor-server-core:3.1.1")
    implementation("io.ktor:ktor-server-core:3.1.1")
    implementation("io.ktor:ktor-serialization-gson:3.1.1")
    implementation("io.ktor:ktor-server-content-negotiation:3.1.1")
    implementation("io.ktor:ktor-server-core:3.1.1")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("commons-codec:commons-codec:$commons_codec_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("org.postgresql:postgresql:$postgresql_version")
    implementation("com.zaxxer:HikariCP:$hikari_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.7.3")
}
