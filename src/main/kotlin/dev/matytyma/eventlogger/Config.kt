package dev.matytyma.eventlogger

import net.kyori.adventure.text.Component
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

object Config {
    private val config = plugin.config

    lateinit var prefix: Component
    lateinit var logger: Logger
    var whitelist: Boolean by Delegates.notNull()
    lateinit var events: Set<LoggerData<*>>

    var alterClassNames: Boolean by Delegates.notNull()
    lateinit var classSeparator: String
    lateinit var classPrefix: String
    lateinit var classPostfix: String

    lateinit var arraySeparator: String
    lateinit var arrayPrefix: String
    lateinit var arrayPostfix: String

    lateinit var fieldSeparator: String

    fun loadConfig() {
        plugin.saveDefaultConfig()
        plugin.reloadConfig()

        prefix = mm.deserialize(config.getString("prefix.general") ?: "<gray>[<gradient:#00F0A0:#00A0F0>EventLogger</gradient>]</gray> ")
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
                }.onFailure { plugin.slF4JLogger.warn("Logger for event '$event' does not exist") }
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
    }
}
