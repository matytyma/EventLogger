package dev.matytyma.eventlogger

import com.akuleshov7.ktoml.annotations.TomlComments
import com.akuleshov7.ktoml.annotations.TomlInlineTable
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.ConfigurationSection

@Serializable
data class Config(
    @TomlComments("List of events to log")
    val whitelist: Set<String> = emptySet(),
    @TomlComments("List of events to exclude from logging, higher priority over whitelist")
    val blacklist: Set<String> = emptySet(),
    val prefix: Prefix = Prefix(),
    val format: Format = Format(),
) {
    @Serializable
    data class Prefix(
        @TomlComments(
            " MiniMessage-styled text, for more informations see https://docs.advntr.dev/minimessage/format.html",
            "and for preview https://webui.advntr.dev, sed for all kinds of plugin output except for event logging"
        )
        val general: String = "<gray>[<gradient:#00F0A0:#00A0F0>EventLogger</gradient>]</gray>",
        val logging: String = "",
    )

    @Serializable
    data class Format(
        // TODO
    )
}


object OldConfig {
    // region Configuration variables
    private val config: Configuration
        get() = plugin.config

    lateinit var prefix: Component
    lateinit var eventLogger: ComponentLogger

    var whitelist: Set<String> = emptySet()
        set(value) {
            field = value
            rebuildEvents()
        }

    var blacklist: Set<String> = emptySet()
        set(value) {
            field = value
            rebuildEvents()
        }

    var alterClassNames: Boolean = true
    var classSeparator: String = ", "
    var classPrefix: String = "("
    var classPostfix: String = ")"

    var arraySeparator: String = ", "
    var arrayPrefix: String = "["
    var arrayPostfix: String = "]"

    var fieldSeparator: String = "="

    var topLeftBorder: Char = '┏'
    var topBorder: Char = '━'
    var topRightBorder: Char = '┓'
    var leftBorder: Char = '┃'￼
    var rightBorder: Char = '┃'
    var bottomLeftBorder: Char = '┗'
    var bottomBorder: Char = '━'
    var bottomRightBorder: Char = '┛'
    // endregion

    fun loadConfig() {

        plugin.saveDefaultConfig()
        plugin.reloadConfig()
        prefix = mm.deserialize(config.getString("prefix.general") ?: "[EventLogger] ")
        eventLogger = ComponentLogger.logger(config.getString("prefix.logging") ?: "")
        whitelist = config.getStringList("whitelist").filterValidEvents()
        blacklist = config.getStringList("blacklist").filterValidEvents()

        val format: ConfigurationSection? = config.getConfigurationSection("format")

        val classFormat: ConfigurationSection? = format?.getConfigurationSection("class")
        alterClassNames = classFormat?.getBoolean("alterNames") ?: alterClassNames
        classSeparator = classFormat?.getString("separator") ?: classSeparator
        classPrefix = classFormat?.getString("prefix") ?: classPrefix
        classPostfix = classFormat?.getString("postfix") ?: classPostfix

        val arrayFormat: ConfigurationSection? = format?.getConfigurationSection("array")
        arraySeparator = arrayFormat?.getString("separator") ?: arraySeparator
        arrayPrefix = arrayFormat?.getString("prefix") ?: arrayPrefix
        arrayPostfix = arrayFormat?.getString("postfix") ?: arrayPostfix

        fieldSeparator = format?.getString("field.separator") ?: fieldSeparator

        val border: ConfigurationSection? = format?.getConfigurationSection("border")
        topLeftBorder = border?.getChar("top-left") ?: topLeftBorder
        topBorder = border?.getChar("top") ?: topBorder
        topRightBorder = border?.getChar("top-right") ?: topRightBorder
        leftBorder = border?.getChar("left") ?: leftBorder
        rightBorder = border?.getChar("right") ?: rightBorder
        bottomLeftBorder = border?.getChar("bottom-left") ?: bottomLeftBorder
        bottomBorder = border?.getChar("bottom") ?: bottomBorder
        bottomRightBorder = border?.getChar("bottom-right") ?: bottomRightBorder
    }

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
        events = (whitelist.mapEvents() - blacklist.mapEvents()).filterNot { it is AbstractLoggerData }.toSet()
    }
}
