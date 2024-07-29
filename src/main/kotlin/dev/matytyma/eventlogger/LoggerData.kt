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
        eventProperties.forEach { (title, value) ->
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
        listOf(
            "Entity" to entity,
            "Direction" to direction
        )
    },
    LoggerData(BlockBreakEvent::class.java) {
        listOf(
            "Player" to player,
            "Drop items" to isDropItems
        )
    },
    LoggerData(BlockBurnEvent::class.java) {
        listOf("Igniting block" to ignitingBlock)
    },
    LoggerData(BlockCanBuildEvent::class.java) {
        listOf(
            "Player" to player,
            "Can build" to isBuildable,
            "Material" to material,
            "Block data" to blockData,
            "Hand" to hand,
        )
    },
)

val groupLoggerData = setOf<GroupLoggerData<*>>(
    GroupLoggerData(BlockEvent::class.java) {
        listOf("Block" to block)
    }
)
