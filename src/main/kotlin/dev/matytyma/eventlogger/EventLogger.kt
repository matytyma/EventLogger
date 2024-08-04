package dev.matytyma.eventlogger

import dev.matytyma.eventlogger.command.ReloadCommand
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.*
import org.bukkit.plugin.IllegalPluginAccessException
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger

lateinit var plugin: EventLogger

class EventLogger : JavaPlugin() {
    val mm = MiniMessage.miniMessage()
    val prefix = "<gray>[<gradient:#00F0A0:#00A0F0>EventLogger</gradient>]</gray>"
    lateinit var logger: Logger
    private val eventList = mutableListOf<LoggerData<*>>()

    fun loadConfig() {
        saveDefaultConfig()

        config.getStringList("events").forEach { event ->
            try {
                eventList += loggerData.first { it.eventClass.simpleName == event }
            } catch (_: NoSuchElementException) {
                logger.warn("Logger for event '$event' does not exist")
            }
        }
    }

    fun registerEvents() {
        val manager = server.pluginManager
        val listener = object : Listener {}
        eventList.forEach {
            val executor = { _: Listener, event: Event -> it.logData(event) }
            try {
                manager.registerEvent(it.eventClass, listener, EventPriority.MONITOR, executor, this)
            } catch(_: IllegalPluginAccessException) {
                logger.warn("Event ${it.eventClass.simpleName} is a group and thus cannot be registered")
            }
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
