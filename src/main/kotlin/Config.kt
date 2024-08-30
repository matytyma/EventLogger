package dev.matytyma.eventlogger

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.ConfigurationSection

object Config {
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
    var leftBorder: Char = '┃'
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
