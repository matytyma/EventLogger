package dev.matytyma.eventlogger

import dev.matytyma.eventlogger.command.MainCommand
import dev.matytyma.eventlogger.command.ReloadCommand
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabExecutor
import org.bukkit.event.*
import org.bukkit.plugin.*
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: EventLogger

val mm: MiniMessage = MiniMessage.miniMessage()

class EventLogger : JavaPlugin() {
    val commands: Map<String, CommandExecutor> = mapOf(
        "eventlogger" to MainCommand,
        "eventloggerreload" to ReloadCommand,
    )

    fun registerEvents() {
        val manager: PluginManager = server.pluginManager
        val listener = object : Listener {}
        Config.events.forEach {
            val executor = EventExecutor { _: Listener, event: Event -> it.logData(event) }
            try {
                manager.registerEvent(it.eventClass, listener, EventPriority.MONITOR, executor, this)
            } catch (e: IllegalPluginAccessException) {
                slF4JLogger.error("An error occurred while registering logger for ${it.eventClass.simpleName}", e)
            }
        }
    }

    private fun registerCommand() = commands.forEach { (name: String, executor: CommandExecutor) ->
        getCommand(name)?.setExecutor(executor)
        if (executor is TabExecutor) {
            getCommand(name)?.tabCompleter = executor
        }
    }

    override fun onEnable() {
        plugin = this
        Config.loadConfig()
        registerEvents()
        registerCommand()
    }
}
