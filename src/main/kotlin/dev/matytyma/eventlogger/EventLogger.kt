package dev.matytyma.eventlogger

import dev.matytyma.eventlogger.command.ReloadCommand
import org.bukkit.command.*
import org.bukkit.event.*
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger

lateinit var plugin: EventLogger

class EventLogger : JavaPlugin() {
    lateinit var logger: Logger
    private val eventList = mutableListOf<LoggerData<*>>()

    fun loadConfig() {
        saveDefaultConfig()

        config.getStringList("events").forEach { event ->
            try {
                eventList += loggerData.first { it.eventClass.simpleName == event }
            } catch (exception: NoSuchElementException) {
                logger.warn("Logger for event '$event' does not exist")
            }
        }
    }

    fun registerEvents() {
        val manager = server.pluginManager
        val listener = object : Listener {}
        eventList.forEach {
            val executor = { _: Listener, event: Event -> it.logData(event) }
            manager.registerEvent(it.eventClass, listener, EventPriority.MONITOR, executor, this)
        }
    }

    override fun onEnable() {
        logger = slF4JLogger
        plugin = this
        loadConfig()
        registerEvents()
        getCommand("elreload")?.setExecutor(ReloadCommand)
    }
}
