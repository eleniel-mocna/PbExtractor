import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.serialization") version "1.9.0"
    application
}

group = "cz.cuni.mff.soukups3"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("GitExtractorKt")
}

tasks.named("run", JavaExec::class) {
    main = "GitExtractorKt"
    classpath = sourceSets["main"].runtimeClasspath
    args = findProperty("execArgs")?.toString()?.split(",") ?: emptyList()
}