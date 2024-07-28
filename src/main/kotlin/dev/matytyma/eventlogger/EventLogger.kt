package dev.matytyma.eventlogger

import org.bukkit.event.Event
import org.bukkit.plugin.java.JavaPlugin

class EventLogger : JavaPlugin() {
    override fun onEnable() {
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
