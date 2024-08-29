package dev.matytyma.eventlogger.command

import dev.matytyma.eventlogger.*
import dev.matytyma.eventlogger.Config.blacklist
import dev.matytyma.eventlogger.Config.whitelist
import org.bukkit.command.*

object WhitelistCommand : TabExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
    ): Boolean {
        val oldEvents = events
        args.forEach { event ->
            if (loggers.any { it.eventClass.simpleName == event }) {
                sender.sendPrefixedMessage("Successfully whitelisted $event")
                whitelist += event
            } else {
                sender.sendPrefixedMessage("Logger for event '$event' does not exist, is it spelled right?")
            }
        }
        (events - oldEvents).forEach { plugin.registerEvent(it) }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
    ): List<String> = loggers.map { it.eventClass.simpleName } - whitelist + blacklist
}
