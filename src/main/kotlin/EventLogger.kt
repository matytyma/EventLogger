package dev.matytyma.eventlogger

import dev.matytyma.eventlogger.command.MainCommand
import dev.matytyma.eventlogger.command.ReloadCommand
import dev.matytyma.minekraft.plugin.on
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabExecutor
import org.bukkit.event.EventPriority
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: EventLogger

val mm: MiniMessage = MiniMessage.miniMessage()

class EventLogger : JavaPlugin() {
    val commands: Map<String, CommandExecutor> = mapOf(
        "eventlogger" to MainCommand,
        "eventloggerreload" to ReloadCommand,
    )

    fun registerEvents() {
        Config.events.forEach {
            plugin.on(it.eventClass, EventPriority.MONITOR) {
                it.logData(this)
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
