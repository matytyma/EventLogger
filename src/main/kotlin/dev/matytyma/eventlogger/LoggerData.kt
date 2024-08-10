package dev.matytyma.eventlogger

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.block.*
import kotlin.math.max

class LoggerData<T : Event>(val eventClass: Class<T>, val properties: T.() -> List<Pair<String, Any?>>) {
    fun logData(event: Event) {
        if (!eventClass.isInstance(event)) return
        val eventProperties = loggerData.filter {
            it.eventClass.isInstance(event)
        }.flatMap { it.getData(event) }.map { it.first to it.second.serialize() }

        val header = "${event.eventName}${if (event is Cancellable && event.isCancelled) " - cancelled" else ""}"
        val width = max(header.length, eventProperties.maxOf {
            (title, value) -> title.length + value.length
        }) + 2

        plugin.logger.info("┏${"━".repeat((width - header.length) / 2)} $header ${"━".repeat((width - header.length + 1) / 2)}┓")
        eventProperties.forEach { (title, value) ->
            val lineWidth = title.length + value.length + 2
            plugin.logger.info("┃ $title: $value ${" ".repeat(width - lineWidth)}┃")
        }
        plugin.logger.info("┗${"━".repeat(width + 2)}┛")
    }

    @Suppress("UNCHECKED_CAST")
    private fun getData(event: Event): List<Pair<String, Any?>> = (event as T).properties()
}

val loggerData = setOf(
    LoggerData(BlockEvent::class.java) {
        listOf("Block" to block)
    },
    LoggerData(BellResonateEvent::class.java) {
        listOf("Resonated entities" to resonatedEntities)
    },
    LoggerData(BellRingEvent::class.java) {
        listOf(
            "Entity" to entity,
            "Direction" to direction,
        )
    },
    LoggerData(BlockBreakEvent::class.java) {
        listOf(
            "Player" to player,
            "Drop items" to isDropItems,
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
    LoggerData(BlockCookEvent::class.java) {
        listOf(
            "Source" to source,
            "Result" to result,
            "Recipe" to recipe,
        )
    },
    LoggerData(BlockDamageAbortEvent::class.java) {
        listOf("Item in hand" to itemInHand)
    },
    LoggerData(BlockDamageEvent::class.java) {
        listOf(
            "Player" to player,
            "Item in hand" to itemInHand,
            "Block face" to blockFace,
            "Instant break" to instaBreak,
        )
    },
)
