package dev.matytyma.eventlogger

import dev.matytyma.eventlogger.command.ReloadCommand
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.*
import org.bukkit.plugin.IllegalPluginAccessException
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: EventLogger

val mm = MiniMessage.miniMessage()

class EventLogger : JavaPlugin() {
    fun registerEvents() {
        val manager = server.pluginManager
        val listener = object : Listener {}
        Config.events.forEach {
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
        Config.loadConfig()
        registerEvents()
        getCommand("elreload")?.setExecutor(ReloadCommand)
    }
}
