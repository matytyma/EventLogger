package dev.matytyma.eventlogger.command

import org.bukkit.command.*


object MainCommand : TabExecutor {
    private val subCommands: Map<String, CommandExecutor> = mapOf(
        "reload" to ReloadCommand,
        "whitelist" to WhitelistCommand,
    )

    private val completers: Map<String, TabCompleter>
        get() = subCommands.filterValues { it is TabExecutor }.mapValues { it.value as TabCompleter }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
    ): Boolean = if (args.isEmpty()) false else {
        subCommands[args[0]]?.onCommand(sender, command, label, args.sliceArray(1..<args.size)) ?: false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
    ): List<String> = (if (args.size == 1) subCommands.keys
    else completers[args[0]]?.onTabComplete(sender, command, label, args))?.filter {
        it.startsWith(args.last())
    } ?: emptyList()
}
