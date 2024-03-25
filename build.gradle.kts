import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    kotlin("jvm") version "1.9.23"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "app.ardeco"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveFileName.set("${rootProject.name}.jar")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("app.ardeco.MainKt") // The main class of the application
}
