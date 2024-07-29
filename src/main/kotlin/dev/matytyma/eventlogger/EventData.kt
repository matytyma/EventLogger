package dev.matytyma.eventlogger

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerMoveEvent

class EventData<T : Event>(val eventClass: Class<T>, val logFunction: (T) -> Unit)

val eventData = setOf(
    EventData(PlayerMoveEvent::class.java) { event -> println(event) }
)

fun logEventData(event: Event, vararg data: Pair<String, Any?>) {
    plugin.logger.debug("[[ ${event.eventName}${if (event is Cancellable && event.isCancelled) " - cancelled" else ""} ]]")
    data.forEach { (title, value) ->
        plugin.logger.info("$title: ${value.serialize()}")
    }
}
