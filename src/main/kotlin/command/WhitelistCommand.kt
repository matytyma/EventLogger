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
        args.forEach { event ->
            if (loggers.any { it.eventClass.simpleName == event}) {
                whitelist += event
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
    ): List<String> = loggers.map { it.eventClass.simpleName } - whitelist + blacklist
}
