package dev.matytyma.eventlogger

import dev.matytyma.eventlogger.Config.prefix
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.configuration.ConfigurationSection

fun Char.repeat(n: Int): String = toString().repeat(n)

fun Audience.sendPrefixedMessage(s: String) = sendPrefixedMessage(mm.deserialize(s))

fun Audience.sendPrefixedMessage(c: Component) = sendMessage(prefix.append(c))

fun ConfigurationSection.getChar(path: String): Char? = getString(path).let {
    if (it?.length == 1) it[0] else {
        plugin.slF4JLogger.warn("Path '${this.currentPath}.$path' does not contain a single character")
        null
    }
}
