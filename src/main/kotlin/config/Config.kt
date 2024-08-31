package dev.matytyma.eventlogger.config

import com.akuleshov7.ktoml.annotations.TomlComments
import dev.matytyma.eventlogger.*
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
data class Config(
    @TomlComments("List of events to log")
    val whitelist: Set<String> = emptySet(),
    @TomlComments("List of events to exclude from logging, higher priority over whitelist")
    val blacklist: Set<String> = emptySet(),
    val prefix: Prefix = Prefix(),
    val format: Format = Format(),
)

@Serializable
data class Prefix(
    @TomlComments(
        " MiniMessage-styled text, for more informations see https://docs.advntr.dev/minimessage/format.html",
        "and for preview https://webui.advntr.dev, sed for all kinds of plugin output except for event logging"
    )
    // @Serializable(with =)
    val general: Component = mm.deserialize("<gray>[<gradient:#00F0A0:#00A0F0>EventLogger</gradient>]</gray>"),
    val logging: String = "",
)

@Serializable
data class Format(

)

@Serializable
data class ClassFormat(
    val alterNames: Boolean = true,
    val prefix: String = "(",
    val separator: String = ", ",
    val postfix: String = ")",
)

private fun Collection<String>.filterValidEvents(): Set<String> = buildSet {
    this@filterValidEvents.forEach { event: String ->
        if (loggers.any { it.eventClass.simpleName == event }) {
            add(event)
        } else {
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
