package dev.matytyma.eventlogger

import dev.matytyma.eventlogger.command.ReloadCommand
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.*
import org.bukkit.plugin.IllegalPluginAccessException
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: EventLogger

class EventLogger : JavaPlugin() {
    val mm = MiniMessage.miniMessage()
    val prefix = mm.deserialize("<gray>[<gradient:#00F0A0:#00A0F0>EventLogger</gradient>]</gray> ")
    private var eventSet = setOf<LoggerData<*>>()

    fun loadConfig() {
        saveDefaultConfig()

        config.getStringList("events").forEach { event ->
            try {
                val loggerData = loggers.first { it.eventClass.simpleName == event }
                if (loggerData is GroupLoggerData) {
                    eventSet += loggers.filter {
                        loggerData.eventClass.isInstance(it)
                    }
                }
                if (loggerData !is ToplevelLoggerData) {
                    eventSet += loggerData
                }
            } catch (_: NoSuchElementException) {
                slF4JLogger.warn("Logger for event '$event' does not exist")
            }
        }

        if (!config.getBoolean("whitelist")) {
            eventSet = (loggers - eventSet).filterNot { it is ToplevelLoggerData }.toSet()
        }
    }

    fun registerEvents() {
        val manager = server.pluginManager
        val listener = object : Listener {}
        eventSet.forEach {
            val executor = { _: Listener, event: Event -> it.logData(event) }
            try {
                manager.registerEvent(it.eventClass, listener, EventPriority.MONITOR, executor, this)
            } catch (e: IllegalPluginAccessException) {
                slF4JLogger.error("An error occurred while registering logger for ${it.eventClass.simpleName}", e)
            }
        }
    }

    override fun onEnable() {
        plugin = this
        loadConfig()
        registerEvents()
        getCommand("elreload")?.setExecutor(ReloadCommand)
    }
}
