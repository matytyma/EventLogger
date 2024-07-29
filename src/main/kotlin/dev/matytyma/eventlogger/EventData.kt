package dev.matytyma.eventlogger

import org.bukkit.event.Cancellable
import org.bukkit.event.Event

class EventData<T : Event>(val eventClass: Class<T>, val logFunction: (T) -> Unit)

val eventData = setOf(
    EventData(PlayerMoveEvent::class.java) { event -> println(event) }
)

fun Event.logData(vararg data: Pair<String, Any?>) {
    plugin.logger.info("[[ $eventName${if (this is Cancellable && isCancelled) " - cancelled" else ""} ]]")
    data.forEach { (title, value) ->
        plugin.logger.info("$title: ${value.serialize()}")
    }
}
