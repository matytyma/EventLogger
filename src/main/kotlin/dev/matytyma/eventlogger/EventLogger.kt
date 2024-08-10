package dev.matytyma.eventlogger

import dev.matytyma.eventlogger.command.ReloadCommand
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.*
import org.bukkit.plugin.IllegalPluginAccessException
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: EventLogger

class EventLogger : JavaPlugin() {
    val mm = MiniMessage.miniMessage()
    val prefix = "<gray>[<gradient:#00F0A0:#00A0F0>EventLogger</gradient>]</gray>"
    private val eventSet = mutableSetOf<LoggerData<*>>()

    fun loadConfig() {
        saveDefaultConfig()

        val whitelist = config.getBoolean("whitelist")
        if (whitelist) eventSet += loggerData

        config.getStringList("events").forEach { event ->
            try {
                val eventLoggerData = loggerData.first { it.eventClass.simpleName == event }
                if (whitelist) {
                    eventSet += eventLoggerData
                } else {
                    eventSet -= eventLoggerData
                }
            } catch (_: NoSuchElementException) {
                slF4JLogger.warn("Logger for event '$event' does not exist")
            }
        }
    }

    fun registerEvents() {
        val manager = server.pluginManager
        val listener = object : Listener {}
        eventSet.forEach {
            val executor = { _: Listener, event: Event -> it.logData(event) }
            try {
                manager.registerEvent(it.eventClass, listener, EventPriority.MONITOR, executor, this)
            } catch (_: IllegalPluginAccessException) {
                slF4JLogger.warn("Event ${it.eventClass.simpleName} is a group and thus cannot be registered")
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
