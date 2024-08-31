package dev.matytyma.eventlogger

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component

fun Char.repeat(n: Int): String = toString().repeat(n)

fun Audience.sendPrefixedMessage(s: String) = sendPrefixedMessage(mm.deserialize(s))

fun Audience.sendPrefixedMessage(c: Component) = sendMessage(plugin.config.logging.plugin.appendSpace().append(c))
