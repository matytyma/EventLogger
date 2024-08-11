package dev.matytyma.eventlogger

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.block.*
import org.slf4j.LoggerFactory
import kotlin.math.max

open class LoggerData<T : Event>(
    val eventClass: Class<T>,
    val properties: T.() -> List<Pair<String, Any?>>,
) {
    companion object {
        private val logger = LoggerFactory.getLogger("")
    }

    fun logData(event: Event) {
        if (!eventClass.isInstance(event)) {
            throw IllegalArgumentException("Event ${event.eventName} could not be passed to logger for ${eventClass.simpleName}")
        }
        val eventProperties = loggers.filter {
            it.eventClass.isInstance(event)
        }.flatMap { it.getData(event) }.map { it.first to it.second.serialize() }

        val header = "${event.eventName}${if (event is Cancellable && event.isCancelled) " - cancelled" else ""}"
        val width = max(header.length, eventProperties.maxOf { (title, value) ->
            title.length + value.length
        }) + 2

        logger.info("┏${"━".repeat((width - header.length) / 2)} $header ${"━".repeat((width - header.length + 1) / 2)}┓")
        eventProperties.forEach { (title, value) ->
            val lineWidth = title.length + value.length + 2
            logger.info("┃ $title: $value ${" ".repeat(width - lineWidth)}┃")
        }
        logger.info("┗${"━".repeat(width + 2)}┛")
    }

    @Suppress("UNCHECKED_CAST")
    private fun getData(event: Event): List<Pair<String, Any?>> = (event as T).properties()
}

open class GroupLoggerData<T : Event>(
    eventClass: Class<T>,
    properties: T.() -> List<Pair<String, Any?>>,
) : LoggerData<T>(eventClass, properties)

class ToplevelLoggerData<T : Event>(
    eventClass: Class<T>,
    properties: T.() -> List<Pair<String, Any?>>,
) : GroupLoggerData<T>(eventClass, properties)

val loggers = setOf(
    ToplevelLoggerData(BlockEvent::class.java) {
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
    GroupLoggerData(BlockCookEvent::class.java) {
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
    LoggerData(BlockDispenseArmorEvent::class.java) {
        listOf("Target entity" to targetEntity)
    },
    GroupLoggerData(BlockDispenseEvent::class.java) {
        listOf(
            "Item" to item,
            "Velocity" to velocity,
        )
    },
    LoggerData(BlockDropItemEvent::class.java) {
        listOf(
            "Player" to player,
            "Items" to items,
            "Block state" to blockState,
        )
    }
)
