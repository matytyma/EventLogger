package dev.matytyma.eventlogger

import org.bukkit.event.*
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

private lateinit var pluginLogger: Logger

fun logEventData(event: Event, vararg data: Pair<String, Any?>) {
    pluginLogger.info("[[ ${event.eventName}${if (event is Cancellable && event.isCancelled) " - cancelled" else ""} ]]")
    data.forEach { (title, value) ->
        pluginLogger.info("$title: ${value.serialize()}")
    }
}

class EventLogger : JavaPlugin() {
    private val eventList = mutableListOf<Class<out Event>>()

    private fun loadConfig() {
        saveDefaultConfig()

        config.getStringList("events").forEach {
            try {
                @Suppress("UNCHECKED_CAST")
                eventList.add(Class.forName("org.bukkit.event.$it") as Class<out Event>)
            } catch (exception: Exception) {
                logger.warning("Event '$it' does not exist")
            }
        }
    }

    private fun registerEvents() {
        val manager = server.pluginManager
        val listener = object : Listener {}
        eventList.forEach {
            println("registering $listener, $it")
            manager.registerEvent(it, listener, EventPriority.MONITOR, { _, event -> /* TODO: Find a suitable `EventData` instance*/ }, this)
        }
    }

    override fun onEnable() {
        pluginLogger = logger
        loadConfig()
        registerEvents()
    }
}
