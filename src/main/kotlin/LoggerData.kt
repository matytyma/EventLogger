package dev.matytyma.eventlogger

import com.destroystokyo.paper.event.entity.EntityTeleportEndGatewayEvent
import com.destroystokyo.paper.event.entity.EntityZapEvent
import io.papermc.paper.event.block.TargetHitEvent
import io.papermc.paper.event.entity.EntityDyeEvent
import io.papermc.paper.event.entity.WaterBottleSplashEvent
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.block.*
import org.bukkit.event.command.UnknownCommandEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.FurnaceStartSmeltEvent
import org.bukkit.event.player.PlayerUnleashEntityEvent
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
        val logger = cfg.logging.events
        with(cfg.format.border) {
            val lines = mutableListOf<String>()
            lines.add("$topLeft${top.repeat((width - header.length) / 2)} $header ${top.repeat((width - header.length + 1) / 2)}$topRight")
            eventProperties.forEach { (title: String, value: String) ->
                val lineWidth = title.length + value.length + 2
                lines.add("$left $title: $value ${" ".repeat(width - lineWidth)}$right")
            }
            lines.add("$bottomLeft${bottom.repeat(width + 2)}$bottomRight")
            lines.forEach { logger.info(mm.deserialize(it)) }
        }
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

val loggers: Set<LoggerData<*>> = setOf(
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
    LoggerData(EntityBreedEvent::class.java) {
        listOf(
            "Breeder" to breeder,
            "Bred with" to bredWith,
            "Mother" to mother,
            "Father" to father,
            "Experience" to experience,
        )
    },
    // region EntityChangeBlockEvent
    GroupLoggerData(EntityChangeBlockEvent::class.java) {
        listOf(
            "Block" to block,
            "New data" to blockData,
            "New material" to to,
        )
    },
    LoggerData(EntityBreakDoorEvent::class.java) {
        emptyList()
    },
    // endregion
    // region EntityCombustEvent
    GroupLoggerData(EntityCombustEvent::class.java) {
        listOf("Duration" to duration)
    },
    LoggerData(EntityCombustByBlockEvent::class.java) {
        listOf("Combuster" to combuster)
    },
    LoggerData(EntityCombustByEntityEvent::class.java) {
        listOf("Combuster" to combuster)
    },
    // endregion
    // region EntityDamageEvent
    GroupLoggerData(EntityDamageEvent::class.java) {
        listOf(
            "Damage" to damage,
            "Final damage" to finalDamage,
            "Cause" to cause,
            // region @Experimental DamageSource
            // "Damage source" to damageSource,
            // endregion
        )
    },
    LoggerData(EntityDamageByBlockEvent::class.java) {
        listOf(
            "Damager" to damager,
            "Damager block state" to damagerBlockState
        )
    },
    LoggerData(EntityDamageByEntityEvent::class.java) {
        listOf(
            "Damager" to damager,
            "Is critical" to isCritical,
        )
    },
    // endregion
    // region EntityDeathEvent
    GroupLoggerData(EntityDeathEvent::class.java) {
        listOf(
            "Drops" to drops,
            "Dropped experience" to droppedExp,
            "Revive health" to reviveHealth,
            "Death sound" to deathSound,
            "Death sound category" to deathSoundCategory,
            "Death sound volume" to deathSoundVolume,
            "Death sound pitch" to deathSoundPitch,
            // region @Experimental DamageSource
            // "Damage source" to damageSource,
            // endregion
        )
    },
    LoggerData(PlayerDeathEvent::class.java) {
        listOf(
            "Death message" to deathMessage(),
            "Items to keep" to itemsToKeep,
            "Keep inventory" to keepInventory,
            "Keep level" to keepLevel,
            "New level" to newLevel,
            "New experience" to newExp,
            "New total experience" to newTotalExp
        )
    },
    // endregion
    LoggerData(EntityDismountEvent::class.java) {
        listOf(
            "Dismounted" to dismounted,
            "Is cancellable" to isCancellable,
        )
    },
    LoggerData(EntityDropItemEvent::class.java) {
        listOf("Item dropped" to itemDrop)
    },
    // region EntityDyeEvent
    GroupLoggerData(EntityDyeEvent::class.java) {
        listOf(
            "Color" to color,
            "Player" to player,
        )
    },
    LoggerData(SheepDyeWoolEvent::class.java) {
        emptyList()
    },
    // endregion
    LoggerData(EntityEnterBlockEvent::class.java) {
        listOf("Block" to block)
    },
    LoggerData(EntityEnterLoveModeEvent::class.java) {
        listOf(
            "Human entity" to humanEntity,
            "Ticks in love" to ticksInLove,
        )
    },
    LoggerData(EntityExhaustionEvent::class.java) {
        listOf(
            "Exhaustion" to exhaustion,
            "Exhaustion reason" to exhaustionReason,
        )
    },
    LoggerData(EntityExplodeEvent::class.java) {
        listOf(
            "Location" to location,
            "Yield" to yield,
            "Blocks" to blockList(),
            // region @Experimental ExplosionResult
            // "Explosion result" to explosionResult,
            // endregion
        )
    },
    LoggerData(EntityInteractEvent::class.java) {
        listOf("Block" to block)
    },
    LoggerData(EntityMountEvent::class.java) {
        listOf("Mount" to mount)
    },
    LoggerData(EntityPickupItemEvent::class.java) {
        listOf(
            "Item" to item,
            "Remaining" to remaining,
        )
    },
    LoggerData(EntityPlaceEvent::class.java) {
        listOf(
            "Player" to player,
            "Block" to block,
            "Block face" to blockFace,
            "Hand" to hand,
        )
    },
    LoggerData(EntityPortalEnterEvent::class.java) {
        listOf(
            "Location" to location,
            "Portal type" to portalType,
        )
    },
    LoggerData(EntityPoseChangeEvent::class.java) {
        listOf("Pose" to pose)
    },
    LoggerData(EntityPotionEffectEvent::class.java) {
        listOf(
            "Modified type" to modifiedType,
            "New effect" to newEffect,
            "Old effect" to oldEffect,
            "Action" to action,
            "Cause" to cause,
            "Is override" to isOverride,
        )
    },
    LoggerData(EntityRegainHealthEvent::class.java) {
        listOf(
            "Amount" to amount,
            "Regain reason" to regainReason,
            "Is fast regen" to isFastRegen,
        )
    },
    LoggerData(EntityResurrectEvent::class.java) {
        listOf("Hand" to hand)
    },
    LoggerData(EntityShootBowEvent::class.java) {
        listOf(
            "Projectile" to projectile,
            "Force" to force,
            "Bow" to bow,
            "Consumable" to consumable,
            "Hand" to hand,
            "Should consume item" to shouldConsumeItem(),
        )
    },
    // region EntitySpawnEvent
    GroupLoggerData(EntitySpawnEvent::class.java) {
        listOf("Location" to location)
    },
    LoggerData(CreatureSpawnEvent::class.java) {
        listOf("Spawn reason" to spawnReason)
    },
    LoggerData(ItemSpawnEvent::class.java) {
        emptyList()
    },
    LoggerData(ProjectileLaunchEvent::class.java) {
        emptyList()
    },
    LoggerData(SpawnerSpawnEvent::class.java) {
        listOf("Spawner" to spawner)
    },
    // region @Experimental TrialSpawnerSpawnEvent
    /* LoggerData(TrialSpawnerSpawnEvent::class.java) {
        listOf("Trial spawner" to trialSpawner)
    }, */
    // endregion
    // endregion
    LoggerData(EntitySpellCastEvent::class.java) {
        listOf("Spell" to spell)
    },
    LoggerData(EntityTameEvent::class.java) {
        listOf("Owner" to owner)
    },
    // region EntityTargetEvent
    GroupLoggerData(EntityTargetEvent::class.java) {
        listOf(
            "Target" to target,
            "Reason" to reason,
        )
    },
    LoggerData(EntityTargetLivingEntityEvent::class.java) {
        emptyList()
    },
    // endregion
    // region EntityTeleportEvent
    GroupLoggerData(EntityTeleportEvent::class.java) {
        listOf(
            "From" to from,
            "To" to to,
        )
    },
    LoggerData(EntityPortalEvent::class.java) {
        listOf(
            "Portal type" to portalType,
            "Can create portal" to canCreatePortal,
            "Creation radius" to creationRadius,
            "Search radius" to searchRadius,
        )
    },
    LoggerData(EntityPortalExitEvent::class.java) {
        listOf(
            "After" to after,
            "Before" to before,
        )
    },
    LoggerData(EntityTeleportEndGatewayEvent::class.java) {
        listOf("Gateway" to gateway)
    },
    // endregion
    LoggerData(EntityToggleGlideEvent::class.java) {
        listOf("Is gliding" to isGliding)
    },
    LoggerData(EntityToggleSwimEvent::class.java) {
        listOf("Is swimming" to isSwimming)
    },
    // region EntityTransformEvent
    GroupLoggerData(EntityTransformEvent::class.java) {
        listOf(
            "Transformed entity" to transformedEntity,
            "Transformed entities" to transformedEntities,
            "Reason" to transformReason,
        )
    },
    // region EntityZapEvent
    GroupLoggerData(EntityZapEvent::class.java) {
        listOf("Bolt" to bolt)
    },
    LoggerData(PigZapEvent::class.java) {
        emptyList()
    },
    // endregion
    // endregion
    // region EntityUnleashEvent
    GroupLoggerData(EntityUnleashEvent::class.java) {
        listOf(
            "Is drop leash" to isDropLeash,
            "Reason" to reason,
        )
    },
    LoggerData(PlayerUnleashEntityEvent::class.java) {
        listOf("Hand" to hand)
    },
    // endregion
    LoggerData(ExplosionPrimeEvent::class.java) {
        listOf(
            "Fire" to fire,
            "Radius" to radius,
        )
    },
    LoggerData(FireworkExplodeEvent::class.java) {
        emptyList()
    },
    LoggerData(FoodLevelChangeEvent::class.java) {
        listOf(
            "Food level" to foodLevel,
            "Item" to item
        )
    },
    LoggerData(HorseJumpEvent::class.java) {
        listOf("Power" to power)
    },
    LoggerData(ItemDespawnEvent::class.java) {
        listOf("Location" to location)
    },
    LoggerData(ItemMergeEvent::class.java) {
        listOf("Target" to target)
    },
    LoggerData(PiglinBarterEvent::class.java) {
        listOf(
            "Input" to input,
            "Outcome" to outcome,
        )
    },
    LoggerData(PigZombieAngerEvent::class.java) {
        listOf(
            "New anger" to newAnger,
            "Target" to target,
        )
    },
    // region ProjectileHitEvent
    GroupLoggerData(ProjectileHitEvent::class.java) {
        listOf(
            "Hit entity" to hitEntity,
            "Hit block" to hitBlock,
            "Hit block face" to hitBlockFace,
        )
    },
    LoggerData(ExpBottleEvent::class.java) {
        listOf(
            "Experience" to experience,
            "Show effect" to showEffect,
        )
    },
    LoggerData(LingeringPotionSplashEvent::class.java) {
        listOf(
            "Area effect cloud" to areaEffectCloud,
            "Allows empty creation" to allowsEmptyCreation(),
        )
    },
    // region PotionSplashEvent
    GroupLoggerData(PotionSplashEvent::class.java) {
        listOf("Affected entities" to affectedEntities)
    },
    LoggerData(WaterBottleSplashEvent::class.java) {
        listOf(
            "To damage" to toDamage,
            "To extinguish" to toExtinguish,
            "To rehydrate" to toRehydrate,
        )
    },
    // endregion
    LoggerData(TargetHitEvent::class.java) {
        listOf("Signal strength" to signalStrength)
    },
    // endregion
    // endregion
)
