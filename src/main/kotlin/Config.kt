package dev.matytyma.eventlogger

import net.kyori.adventure.text.Component
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.Event
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

object Config {
    // region Configuration variables
    private val config: Configuration
        get() = plugin.config

    lateinit var prefix: Component
    lateinit var logger: Logger
    var events: Set<LoggerData<*>> = emptySet()

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

    lateinit var classFormatter: Any.(List<Pair<String, Any?>>) -> String

    var alterClassNames: Boolean = true
    var classSeparator: String = ", "
    var classPrefix: String = "("
    var classPostfix: String = ")"

    var arraySeparator: String = ", "
    var arrayPrefix: String = "]"
    var arrayPostfix: String =

    var fieldSeparator: String

    var topLeftBorder: Char by Delegates.notNull()
    var topBorder: Char by Delegates.notNull()
    var topRightBorder: Char by Delegates.notNull()
    var leftBorder: Char by Delegates.notNull()
    var rightBorder: Char by Delegates.notNull()
    var bottomLeftBorder: Char by Delegates.notNull()
    var bottomBorder: Char by Delegates.notNull()
    var bottomRightBorder: Char by Delegates.notNull()
    // endregion

    fun loadConfig() {
        plugin.saveDefaultConfig()
        plugin.reloadConfig()
        prefix = mm.deserialize(config.getString("prefix.general") ?: "[EventLogger] ")
        logger = LoggerFactory.getLogger(config.getString("prefix.logging"))
        whitelist = config.getStringList("whitelist").toSet()
        blacklist = config.getStringList("blacklist").toSet()

        val format: ConfigurationSection? = config.getConfigurationSection("format")

        val classFormat: ConfigurationSection? = format?.getConfigurationSection("class")
        alterClassNames = classFormat?.getBoolean("alterNames") ?: alterClassNames
        classSeparator = classFormat?.getString("separator") ?: classSeparator
        classPrefix = classFormat?.getString("prefix") ?: "("
        classPostfix = classFormat?.getString("postfix") ?: ")"

        val arrayFormat: ConfigurationSection? = format?.getConfigurationSection("array")
        arraySeparator = arrayFormat?.getString("separator") ?: ", "
        arrayPrefix = arrayFormat?.getString("prefix") ?: "["
        arrayPostfix = arrayFormat?.getString("postfix") ?: "]"

        fieldSeparator = format?.getString("field.separator") ?: "="

        val border: ConfigurationSection? = format?.getConfigurationSection("border")
        topLeftBorder = border?.getChar("top-left") ?: '┏'
        topBorder = border?.getChar("top") ?: '━'
        topRightBorder = border?.getChar("top-right") ?: '┓'
        leftBorder = border?.getChar("left") ?: '┃'
        rightBorder = border?.getChar("right") ?: '┃'
        bottomLeftBorder = border?.getChar("bottom-left") ?: '┗'
        bottomBorder = border?.getChar("bottom") ?: '━'
        bottomRightBorder = border?.getChar("bottom-right") ?: '┛'
    }

    private fun mapEvents(events: Set<String>): Set<LoggerData<*>> = events.flatMap { event: String ->
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
        events = (mapEvents(whitelist) - mapEvents(blacklist)).filterNot { it is AbstractLoggerData }.toSet()
    }
}
