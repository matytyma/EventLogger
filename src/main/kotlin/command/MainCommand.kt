package dev.matytyma.eventlogger.command

import dev.matytyma.eventlogger.plugin
import org.bukkit.command.*


object MainCommand : TabExecutor {
    private val commands: Map<String, CommandExecutor>
        get() = plugin.commands
    private val completers: Map<String, TabCompleter>
        get() = commands.filterValues { it is TabExecutor }.mapValues { it.value as TabCompleter }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
    ): Boolean = if (args.isEmpty()) false else {
        commands[args[0]]?.onCommand(sender, command, label, args.sliceArray(1..<args.size)) ?: false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
    ): List<String> = (if (args.size == 1) commands.keys
    else completers[args[0]]?.onTabComplete(sender, command, label, args))?.filter {
        it.startsWith(args.last())
    } ?: emptyList()
}
