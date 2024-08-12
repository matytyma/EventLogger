package dev.matytyma.eventlogger

import net.kyori.adventure.text.Component
import org.bukkit.configuration.ConfigurationSection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

object Config {
    private val config = plugin.config

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

    fun loadConfig() {
        plugin.saveDefaultConfig()
        plugin.reloadConfig()

        prefix = mm.deserialize(
            config.getString("prefix.general") ?: "<gray>[<gradient:#00F0A0:#00A0F0>EventLogger</gradient>]</gray> "
        )
        logger = LoggerFactory.getLogger(config.getString("prefix.logging"))
        whitelist = config.getBoolean("whitelist")
        events = config.getStringList("events").flatMap { event ->
            buildList {
                runCatching {
                    val loggerData = loggers.first { it.eventClass.simpleName == event }
                    if (loggerData is GroupLoggerData) {
                        addAll(loggers.filter {
                            loggerData.eventClass.isAssignableFrom(it.eventClass)
                        } - loggerData)
                    }
                    if (loggerData !is ToplevelLoggerData) {
                        add(loggerData)
                    }
                }.onFailure { plugin.slF4JLogger.warn("Logger for event '$event' does not exist, is it spelled right?") }
            }
        }.let { events ->
            return@let if (!config.getBoolean("whitelist")) {
                (loggers - events.toSet()).filterNot { it is ToplevelLoggerData<*> }
            } else events
        }.toSet()

        val format = config.getConfigurationSection("format")

        val classFormat = format?.getConfigurationSection("class")
        alterClassNames = classFormat?.getBoolean("alterNames") ?: true
        classSeparator = classFormat?.getString("separator") ?: ", "
        classPrefix = classFormat?.getString("prefix") ?: "("
        classPostfix = classFormat?.getString("postfix") ?: ")"

        val arrayFormat = format?.getConfigurationSection("array")
        arraySeparator = arrayFormat?.getString("separator") ?: ", "
        arrayPrefix = arrayFormat?.getString("prefix") ?: "["
        arrayPostfix = arrayFormat?.getString("postfix") ?: "]"

        fieldSeparator = format?.getString("field.separator") ?: "="

        val border = format?.getConfigurationSection("border")
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

fun ConfigurationSection.getChar(path: String): Char? = getString(path).let {
    if (it?.length == 1) it[0] else {
        plugin.slF4JLogger.warn("Path '${this.currentPath}.$path' does not contain a single character")
        null
    }
}
