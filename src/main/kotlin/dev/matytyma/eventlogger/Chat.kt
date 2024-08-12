package dev.matytyma.eventlogger

import dev.matytyma.eventlogger.Config.prefix
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component

fun Audience.sendPrefixedMessage(s: String) = sendPrefixedMessage(mm.deserialize(s))

fun Audience.sendPrefixedMessage(c: Component) = sendMessage(prefix.append(c))
