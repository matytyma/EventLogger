package dev.matytyma.eventlogger.command

import dev.matytyma.eventlogger.*
import org.bukkit.command.*
import org.bukkit.event.HandlerList

object ReloadCommand : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        Config.loadConfig()
        HandlerList.unregisterAll(plugin)
        plugin.registerEvents()
        sender.sendPrefixedMessage("Reload complete.")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
    ): List<String> = emptyList()
}
