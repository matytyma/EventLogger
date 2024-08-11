package dev.matytyma.eventlogger

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component

fun Audience.sendPrefixedMessage(s: String) = sendPrefixedMessage(plugin.mm.deserialize(s))

fun Audience.sendPrefixedMessage(c: Component) = sendMessage(plugin.prefix.append(c))
