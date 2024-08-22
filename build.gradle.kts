plugins {
    kotlin("jvm") version "2.0.0"
}

group = "dev.matytyma.eventlogger"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    implementation("dev.matytyma.minekraft:minekraft-api:1.0-SNAPSHOT")
    compileOnly(kotlin("stdlib"))
}

val targetJavaVersion = 21

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
