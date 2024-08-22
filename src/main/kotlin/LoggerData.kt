package dev.matytyma.eventlogger

import dev.matytyma.eventlogger.Config.bottomBorder
import dev.matytyma.eventlogger.Config.bottomLeftBorder
import dev.matytyma.eventlogger.Config.bottomRightBorder
import dev.matytyma.eventlogger.Config.leftBorder
import dev.matytyma.eventlogger.Config.logger
import dev.matytyma.eventlogger.Config.rightBorder
import dev.matytyma.eventlogger.Config.topBorder
import dev.matytyma.eventlogger.Config.topLeftBorder
import dev.matytyma.eventlogger.Config.topRightBorder
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.block.*
import kotlin.math.max

open class LoggerData<T : Event>(
    val eventClass: Class<T>,
    val properties: T.() -> List<Pair<String, Any?>>,
) {
    fun logData(event: Event) {
        if (!eventClass.isInstance(event)) {
            throw IllegalArgumentException("Event ${event.eventName} could not be passed to logger for ${eventClass.simpleName}")
        }
        val eventProperties: List<Pair<String, String>> = loggers.filter {
            it.eventClass.isInstance(event)
        }.flatMap { it.getData(event) }.map { it.first to it.second.serialize() }

        val header = "${event.eventName}${if (event is Cancellable && event.isCancelled) " - cancelled" else ""}"
        val width = max(header.length, eventProperties.maxOf { (title: String, value: String) ->
            title.length + value.length
        }) + 2

        logger.info("$topLeftBorder${topBorder.repeat((width - header.length) / 2)} $header ${topBorder.repeat((width - header.length + 1) / 2)}$topRightBorder")
        eventProperties.forEach { (title: String, value: String) ->
            val lineWidth = title.length + value.length + 2
            logger.info("$leftBorder $title: $value ${" ".repeat(width - lineWidth)}$rightBorder")
        }
        logger.info("$bottomLeftBorder${bottomBorder.repeat(width + 2)}$bottomRightBorder")
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

val loggers: Set<LoggerData<out Event>> = setOf(
    // region Block events
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
    // region BlockDispenseEvent
    GroupLoggerData(BlockDispenseEvent::class.java) {
        listOf(
            "Item" to item,
            "Velocity" to velocity,
        )
    },
    LoggerData(BlockDispenseArmorEvent::class.java) {
        listOf("Target entity" to targetEntity)
    },
    // endregion
    LoggerData(BlockDropItemEvent::class.java) {
        listOf(
            "Player" to player,
            "Items" to items,
            "Block state" to blockState,
        )
    },
    // region BlockExpEvent
    GroupLoggerData(BlockExpEvent::class.java) {
        listOf("Exp to drop" to expToDrop)
    },
    LoggerData(BlockBreakEvent::class.java) {
        listOf(
            "Player" to player,
            "Drop items" to isDropItems,
        )
    },
    // endregion
    LoggerData(BlockExplodeEvent::class.java) {
        listOf(
            "Affected blocks" to blockList(),
            "Exploded block state" to explodedBlockState,
            // TODO: uncomment once `ExplosionResult` is stable
            // "Explosion result" to explosionResult,
            "Yield" to yield,
        )
    },
    LoggerData(BlockFadeEvent::class.java) {
        listOf("New state" to newState)
    },
    LoggerData(BlockFertilizeEvent::class.java) {
        listOf(
            "Player" to player,
            "Affected blocks" to blocks,
        )
    },
    LoggerData(BlockFromToEvent::class.java) {
        listOf(
            "To block" to toBlock,
            "Face" to face,
        )
    },
    // region BlockGrowEvent
    LoggerData(BlockGrowEvent::class.java) {
        listOf("New state" to newState)
    },
    LoggerData(BlockFormEvent::class.java) {
        emptyList()
    },
    // endregion
    LoggerData(BlockIgniteEvent::class.java) {
        listOf(
            "Igniting entity" to ignitingEntity,
            "Igniting block" to ignitingBlock,
            "Player" to player,
            "Cause" to cause,
        )
    },
    LoggerData(BlockMultiPlaceEvent::class.java) {
        listOf("Replaced block states" to replacedBlockStates)
    },
    LoggerData(BlockPhysicsEvent::class.java) {
        listOf(
            "Source block" to sourceBlock,
            "Changed type" to changedType,
            "Changed block data" to changedBlockData
        )
    },
    // endregion
)