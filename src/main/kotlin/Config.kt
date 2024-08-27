package dev.matytyma.eventlogger

import net.kyori.adventure.text.Component
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.ConfigurationSection
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Config {
    // region Configuration variables
    private val config: Configuration
        get() = plugin.config

    lateinit var prefix: Component
    lateinit var eventLogger: Logger

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
        eventLogger = LoggerFactory.getLogger(config.getString("prefix.logging"))
        whitelist = config.getStringList("whitelist").let {>
            buildSet {
                it.forEach {

                }
                if (loggers.any { it.eventClass.simpleName == it}) {
                    plugin.slF4JLogger.warn("Logger for event '$it' does not exist, is it spelled right?")
                    add(event)
                } else {
                    sender.sendPrefixedMessage("No logger found for $event")
                }
            }
        }
        blacklist = config.getStringList("blacklist").toSet()

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
