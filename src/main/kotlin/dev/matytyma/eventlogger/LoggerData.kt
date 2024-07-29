package dev.matytyma.eventlogger

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.block.*

class LoggerData<T : Event>(val eventClass: Class<T>, val properties: T.() -> List<Pair<String, Any?>>) {
    @Suppress("UNCHECKED_CAST")
    fun logData(event: Event) {
        if (!eventClass.isInstance(event)) return
        val eventProperties = groupLoggerData.filter {
            it.eventClass.isInstance(event)
        }.flatMap { it.getData(event) } + (event as T).properties()
        plugin.logger.info("[[ ${event.eventName}${if (event is Cancellable && event.isCancelled) " - cancelled" else ""} ]]")
        eventProperties.forEach {  (title, value) ->
            plugin.logger.info("$title: ${value.serialize()}")
        }
    }
}

class GroupLoggerData<T : Event>(val eventClass: Class<T>, val properties: T.() -> List<Pair<String, Any?>>) {
    @Suppress("UNCHECKED_CAST")
    fun getData(event: Event): List<Pair<String, Any?>> = (event as T).properties()
}

val loggerData = setOf(
    LoggerData(BellResonateEvent::class.java) {
        listOf("Resonated entities" to resonatedEntities)
    },
    LoggerData(BellRingEvent::class.java) {
        listOf("Direction" to direction, "entity" to entity)
    },
)

val groupLoggerData = setOf<GroupLoggerData<*>>(
    GroupLoggerData(BlockEvent::class.java) {
        listOf("Block" to block)
    }
)
