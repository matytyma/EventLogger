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
import org.bukkit.event.command.UnknownCommandEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.FurnaceStartSmeltEvent
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

class AbstractLoggerData<T : Event>(
    eventClass: Class<T>,
    properties: T.() -> List<Pair<String, Any?>>,
) : GroupLoggerData<T>(eventClass, properties)

val loggers: Set<LoggerData<out Event>> = setOf(
    // region Block events
    AbstractLoggerData(BlockEvent::class.java) {
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
    GroupLoggerData(BlockGrowEvent::class.java) {
        listOf("New state" to newState)
    },
    // region BlockFormEvent
    GroupLoggerData(BlockFormEvent::class.java) {
        emptyList()
    },
    LoggerData(BlockSpreadEvent::class.java) {
        listOf("Source" to source)
    },
    LoggerData(EntityBlockFormEvent::class.java) {
        listOf("Entity" to entity)
    },
    // endregion
    // endregion
    LoggerData(BlockIgniteEvent::class.java) {
        listOf(
            "Igniting entity" to ignitingEntity,
            "Igniting block" to ignitingBlock,
            "Player" to player,
            "Cause" to cause,
        )
    },
    LoggerData(BlockPhysicsEvent::class.java) {
        listOf(
            "Source block" to sourceBlock,
            "Changed type" to changedType,
            "Changed block data" to changedBlockData
        )
    },
    // region BlockPistonEvent
    AbstractLoggerData(BlockPistonEvent::class.java) {
        listOf(
            "Direction" to direction,
            "Is sticky" to isSticky,
        )
    },
    LoggerData(BlockPistonExtendEvent::class.java) {
        listOf("Blocks" to blocks)
    },
    LoggerData(BlockPistonRetractEvent::class.java) {
        listOf("Blocks" to blocks)
    },
    // endregion
    // region BlockPlaceEvent
    GroupLoggerData(BlockPlaceEvent::class.java) {
        listOf(
            "Player" to player,
            "Item in hand" to itemInHand,
            "Can build" to canBuild(),
            "Placed block" to blockPlaced,
            "Hand" to hand,
            "Replaced block state" to blockReplacedState,
            "Block placed against" to blockAgainst,
        )
    },
    LoggerData(BlockMultiPlaceEvent::class.java) {
        listOf("Replaced block states" to replacedBlockStates)
    },
    // endregion
    LoggerData(BlockReceiveGameEvent::class.java) {
        listOf(
            "Entity" to entity,
            "Game event" to event,
        )
    },
    LoggerData(BlockRedstoneEvent::class.java) {
        listOf(
            "New current" to newCurrent,
            "Old current" to oldCurrent,
        )
    },
    LoggerData(BlockShearEntityEvent::class.java) {
        listOf(
            "Entity" to entity,
            "Drops" to drops,
            "Tool" to tool,
        )
    },
    LoggerData(CauldronLevelChangeEvent::class.java) {
        listOf(
            "Entity" to entity,
            "New state" to newState,
            "Reason" to reason,
        )
    },
    LoggerData(CrafterCraftEvent::class.java) {
        listOf(
            "Recipe" to recipe,
            "Result" to result,
        )
    },
    LoggerData(FluidLevelChangeEvent::class.java) {
        listOf("New data" to newData)
    },
    // region InventoryBlockStartEvent
    GroupLoggerData(InventoryBlockStartEvent::class.java) {
        listOf("Source" to source)
    },
    // region @Experimental BrewingStartEvent, CampfireStartEvent
    /* LoggerData(BrewingStartEvent::class.java) {
        listOf("Total brewing time" to totalBrewTime)
    },
    LoggerData(CampfireStartEvent::class.java) {
        listOf(
            "Recipe" to recipe,
            "Total cooking time" to totalCookTime,
        )
    }, */
    // endregion
    LoggerData(FurnaceStartSmeltEvent::class.java) {
        listOf(
            "Recipe" to recipe,
            "Total cooking time" to totalCookTime,
        )
    },
    // endregion
    LoggerData(LeavesDecayEvent::class.java) {
        emptyList()
    },
    LoggerData(MoistureChangeEvent::class.java) {
        listOf("New state" to newState)
    },
    LoggerData(NotePlayEvent::class.java) {
        listOf(
            "Instrument" to instrument,
            "Note" to note,
        )
    },
    LoggerData(SculkBloomEvent::class.java) {
        listOf("Charge" to charge)
    },
    LoggerData(SignChangeEvent::class.java) {
        listOf(
            "Player" to player,
            "Lines" to lines(),
            "Side" to side,
        )
    },
    LoggerData(SpongeAbsorbEvent::class.java) {
        listOf("Blocks" to blocks)
    },
    // region @Experimental VaultDisplayItemEvent
    /* LoggerData(VaultDisplayItemEvent::class.java) {
        listOf("Display item" to displayItem)
    }, */
    // endregion
    LoggerData(TNTPrimeEvent::class.java) {
        listOf(
            "Priming block" to primingBlock,
            "Priming entity" to primingEntity,
            "Cause" to cause,
        )
    },
    // endregion
    // region Command events
    LoggerData(UnknownCommandEvent::class.java) {
        listOf(
            "Sender" to sender,
            "Command line" to commandLine,
            "Message" to message(),
        )
    },
    // endregion
    // region Entity events
    AbstractLoggerData(EntityEvent::class.java) {
        listOf("Entity" to entity)
    },
    LoggerData(AreaEffectCloudApplyEvent::class.java) {
        listOf("Affected entities" to affectedEntities)
    },
    LoggerData(ArrowBodyCountChangeEvent::class.java) {
        listOf(
            "New amount" to newAmount,
            "Old amount" to oldAmount,
            "Is reset" to isReset,
        )
    },
    LoggerData(BatToggleSleepEvent::class.java) {
        listOf("Is awake" to isAwake)
    },
    LoggerData(CreeperPowerEvent::class.java) {
        listOf(
            "Lightning" to lightning,
            "Cause" to cause,
        )
    },
    LoggerData(EnderDragonChangePhaseEvent::class.java) {
        listOf(
            "New phase" to newPhase,
            "Current phase" to currentPhase,
        )
    },
    LoggerData(EntityAirChangeEvent::class.java) {
        listOf("Amount" to amount)
    },
    // endregion
)
