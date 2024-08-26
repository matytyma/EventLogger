val kotlinVersion = runCatching { properties["kotlinVersion"] as String }.getOrElse { error("Invalid Kotlin version") }
val minecraftVersion = runCatching { properties["minecraftVersion"] as String }.getOrElse { error("Invalid Minecraft version") }

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.gradleup.shadow") version "8.3.0"
}

group = "dev.matytyma.eventlogger"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
    implementation("dev.matytyma.minekraft:minekraft-api:1.0-SNAPSHOT")
}

val targetJavaVersion = 21

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    val props = mapOf(
        "version" to version,
        "apiVersion" to minecraftVersion,
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    minimize()
    dependencies {
        include(dependency("dev.matytyma.minekraft:minekraft-api"))
    }
}
