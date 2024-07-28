package dev.matytyma.eventlogger

import org.bukkit.event.Event
import org.bukkit.event.player.PlayerMoveEvent

class EventData<T : Event>(val eventClass: Class<T>, val logFunction: (T) -> Unit)

val eventData = setOf(
    EventData(PlayerMoveEvent::class.java) { event -> println(event) }
)
