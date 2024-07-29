package dev.matytyma.eventlogger

import org.bukkit.event.*
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger

lateinit var plugin: EventLogger

class EventLogger : JavaPlugin() {
    lateinit var logger: Logger
    private val eventList = mutableListOf<EventData<*>>()

    private fun loadConfig() {
        saveDefaultConfig()

        config.getStringList("events").forEach { event ->
            try {
                eventList += eventData.first { it.eventClass.simpleName == event }
            } catch (exception: NoSuchElementException) {
                logger.warn("Logger for event '$event' does not exist")
            }
        }
    }

    private fun <T : Event> logEvent(eventData: EventData<T>, event: Event) {
        if (!eventData.eventClass.isInstance(event)) return
        @Suppress("UNCHECKED_CAST") eventData.logFunction(event as T)
    }

    private fun registerEvents() {
        val manager = server.pluginManager
        val listener = object : Listener {}
        eventList.forEach {
            val executor = { _: Listener, event: Event -> logEvent(it, event) }
            manager.registerEvent(it.eventClass, listener, EventPriority.MONITOR, executor, this)
        }
    }

    override fun onEnable() {
        logger = slF4JLogger
        plugin = this
        loadConfig()
        registerEvents()
    }
}
