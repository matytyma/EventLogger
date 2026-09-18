plugins {
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.serialization") version "2.4.10"
    id("com.gradleup.shadow") version "9.6.1"
    idea
}

group = "dev.matytyma.eventlogger"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

val kotlinVersion = runCatching { property("kotlinVersion").toString() }.getOrElse { error("Invalid Kotlin version") }
val paperVersion = runCatching { property("paperVersion").toString() }.getOrElse { error("Invalid Minecraft version") }
val ktomlVersion = runCatching { property("ktomlVersion").toString() }.getOrElse { error("Invalid KToml version") }

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperVersion.build.+")
}

val targetJavaVersion = 25

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    val props = mapOf(
        "version" to version,
        "apiVersion" to paperVersion,
        "libraries" to listOf(
            "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion",
            "com.akuleshov7:ktoml-core-jvm:$ktomlVersion",
            "com.akuleshov7:ktoml-file-jvm:$ktomlVersion",
        ).joinToString(",\n  ")
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
    minimize()
}

idea.module {
    isDownloadSources = true
    isDownloadJavadoc = true
}
