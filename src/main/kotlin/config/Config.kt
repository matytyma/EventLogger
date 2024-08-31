package dev.matytyma.eventlogger.config

import com.akuleshov7.ktoml.annotations.*
import dev.matytyma.eventlogger.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

@Serializable
data class Config(
    @TomlComments("List of events to log")
    @TomlMultiline
    val whitelist: Set<String> = emptySet(),
    @TomlComments("List of events to exclude from logging, higher priority over whitelist")
    @TomlMultiline
    val blacklist: Set<String> = emptySet(),
    @SerialName("prefix")
    val logging: Logging = Logging(),
    val format: Format = Format(),
) {
    val classFormat: ClassFormat
        get() = format.`class`

    val arrayFormat: ArrayFormat
        get() = format.array

    val fieldFormat: FieldFormat
        get() = format.field
}

@Serializable
data class Logging(
    @TomlComments(
        "MiniMessage-styled text, for more information see https://docs.advntr.dev/minimessage/format.html",
        "and for preview https://webui.advntr.dev",
        "Used for all kinds of plugin output except for event logging",
    )
    @SerialName("plugin")
    val pluginRaw: String ="<gray>[<gradient:#00F0A0:#00A0F0>EventLogger</gradient>]</gray>",
    @Serializable(with = ComponentLoggerAsPrefixSerializer::class)
    val events: ComponentLogger = ComponentLogger.logger(""),
) {
    val plugin: Component
        get() = mm.deserialize(pluginRaw)
}

@Serializable
data class Format(
    val `class`: ClassFormat = ClassFormat(),
    val array: ArrayFormat = ArrayFormat(),
    val field: FieldFormat = FieldFormat(),
    val border: BorderFormat = BorderFormat(),
)

@Serializable
data class ClassFormat(
    val alterNames: Boolean = true,
    val prefix: String = "(",
    val separator: String = ", ",
    val postfix: String = ")",
)

@Serializable
data class ArrayFormat(
    val prefix: String = "[",
    val separator: String = ", ",
    val postfix: String = "]",
)

@Serializable
data class FieldFormat(
    val separator: String = "=",
)

@Serializable
data class BorderFormat(
    val topLeft: String = "┏",
    val top: String = "━",
    val topRight: String = "┓",
    val left: String = "┃",
    val right: String = "┃",
    val bottomLeft: String = "┗",
    val bottom: String = "━",
    val bottomRight: String = "┛",
)

@Serializable
data class Theme(

)

private fun Collection<String>.filterValidEvents(shouldLog: Boolean): Set<String> = buildSet {
    this@filterValidEvents.forEach { event: String ->
        if (loggers.any { it.eventClass.simpleName == event }) {
            add(event)
        } else if (shouldLog) {
            plugin.slF4JLogger.warn("Logger for event '$event' does not exist, is it spelled right?")
        }
    }
}

private fun Set<String>.mapEvents(): Set<LoggerData<*>> = this.flatMap { event: String ->
    buildSet {
        val loggerData: LoggerData<*> = loggers.find { it.eventClass.simpleName == event } ?: return@buildSet
        if (loggerData is GroupLoggerData) {
            addAll(loggers.filter {
                loggerData.eventClass.isAssignableFrom(it.eventClass)
            } - loggerData)
        }
        if (loggerData !is AbstractLoggerData) {
            add(loggerData)
        }
    }
}.toSet()

private fun rebuildEvents() {
    events = (cfg.whitelist.mapEvents() - cfg.blacklist.mapEvents()).filterNot { it is AbstractLoggerData }.toSet()
}
