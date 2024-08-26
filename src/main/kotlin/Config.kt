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
    private var whitelist: Boolean by Delegates.notNull()
    lateinit var events: Set<LoggerData<*>>

    var alterClassNames: Boolean by Delegates.notNull()
    lateinit var classSeparator: String
    lateinit var classPrefix: String
    lateinit var classPostfix: String

    lateinit var arraySeparator: String
    lateinit var arrayPrefix: String
    lateinit var arrayPostfix: String

    lateinit var fieldSeparator: String

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
        whitelist = config.getBoolean("whitelist")
        events = config.getStringList("events").flatMap { event: String ->
            buildList {
                try {
                    val loggerData: LoggerData<*> = loggers.first { it.eventClass.simpleName == event }
                    if (loggerData is GroupLoggerData) {
                        addAll(loggers.filter {
                            loggerData.eventClass.isAssignableFrom(it.eventClass)
                        } - loggerData)
                    }
                    if (loggerData !is AbstractLoggerData) {
                        add(loggerData)
                    }
                } catch (_: Throwable) {
                    plugin.slF4JLogger.warn("Logger for event '$event' does not exist, is it spelled right?")
                }
            }
        }.let { events: List<LoggerData<out Event>> ->
            if (!config.getBoolean("whitelist")) {
                (loggers - events.toSet()).filterNot { it is AbstractLoggerData<*> }
            } else events
        }.toSet()

        val format: ConfigurationSection? = config.getConfigurationSection("format")

        val classFormat: ConfigurationSection? = format?.getConfigurationSection("class")
        alterClassNames = classFormat?.getBoolean("alterNames") ?: true
        classSeparator = classFormat?.getString("separator") ?: ", "
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
}
