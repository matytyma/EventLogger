package dev.matytyma.eventlogger.command

import dev.matytyma.eventlogger.plugin
import dev.matytyma.eventlogger.sendPrefixedMessage
import org.bukkit.command.*
import org.bukkit.event.HandlerList

object ReloadCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        plugin.loadConfig()
        plugin.registerEvents()
        HandlerList.unregisterAll(plugin)
        sender.sendPrefixedMessage("Reload complete.")
        return true
    }
}
