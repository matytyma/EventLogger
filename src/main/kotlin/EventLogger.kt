package dev.matytyma.eventlogger

import dev.matytyma.eventlogger.command.*
import dev.matytyma.minekraft.plugin.on
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandExecutor
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: EventLogger

val mm: MiniMessage = MiniMessage.miniMessage()

var events: Set<LoggerData<*>> = emptySet()

class EventLogger : JavaPlugin() {
    val listeners: MutableMap<LoggerData<*>, Listener> = mutableMapOf()

    val commands: Map<String, CommandExecutor> = mapOf(
        "reload" to ReloadCommand,
        "whitelist" to WhitelistCommand,
    )

    fun registerEvent(logger: LoggerData<*>) {
        listeners[logger] = plugin.on(logger.eventClass, EventPriority.MONITOR) {
            logger.logData(this)
        }
    }

    fun registerEvents() = events.forEach(::registerEvent)

    override fun onEnable() {
        plugin = this
        Config.loadConfig()
        registerEvents()
        getCommand("el")?.apply {
            setExecutor(MainCommand)
            tabCompleter = MainCommand
        }
    }
}
