package dev.matytyma.eventlogger

import org.bukkit.event.Event
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

private lateinit var pluginLogger: Logger

fun logEventData(event: Event, vararg data: Pair<String, Any?>) {
    pluginLogger.info("[[ ${event.eventName} ]]")
    data.forEach { (title, value) ->
        pluginLogger.info("$title: ${value.serialize()}")
    }
}

class EventLogger : JavaPlugin() {
    override fun onEnable() {
        pluginLogger = logger
        saveDefaultConfig()

        val eventList = mutableListOf<Class<out Event>>()

        config.getStringList("events").forEach {
            try {
                @Suppress("UNCHECKED_CAST")
                eventList.add(Class.forName("org.bukkit.event.$it") as Class<out Event>)
            } catch (exception: Exception) {
                logger.warning("Event '$it' does not exist")
            }
        }
    }
}
