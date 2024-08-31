package dev.matytyma.eventlogger

import dev.matytyma.eventlogger.config.ArrayFormat
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Any?.serialize(): String {
    val properties: List<Pair<String, Any?>> = when (this) {
        is Block -> listOf("position" to listOf(x, y, z), "type" to type, "data" to blockData)
        is Location -> listOf("world" to world, "position" to listOf(x, y, z), "yaw" to yaw, "pitch" to pitch)
        is Player -> listOf("name" to name, "id" to uniqueId)
        is World -> listOf("name" to name)
        is BlockData -> listOf("data" to getAsString(true))
        is ItemStack -> listOf("type" to type, "amount" to amount, "meta" to itemMeta)
        is Item -> listOf("location" to location, "stack" to itemStack)
        else -> emptyList()
    }
    return if (properties.isEmpty()) toString() else this!!.formatClass(properties)
}

private fun Any.formatClass(properties: List<Pair<String, Any?>>): String = with(cfg.classFormat) {
    "${javaClass.alteredName.color(theme.`class`)})${
        properties.joinToString(
            separator.color(theme.special), prefix.color(theme.special), postfix.color(theme.special)
        ) { (name: String, value: Any?) -> "$name${cfg.fieldFormat.separator}${value.formatValue()}" }
    }"
}

private fun Any?.formatValue(): String = cfg.arrayFormat.let { fmt: ArrayFormat ->
    when (this) {
        is Number -> toString()
        is CharSequence -> toString()
        is Boolean -> toString()
        is Collection<*> -> joinToString(fmt.separator, fmt.prefix, fmt.postfix) { it.formatValue() }
        is Array<*> -> joinToString(fmt.separator, fmt.prefix, fmt.postfix) { it.formatValue() }
        is ByteArray -> joinToString(fmt.separator, fmt.prefix, fmt.postfix)
        is CharArray -> joinToString(fmt.separator, fmt.prefix, fmt.postfix)
        is ShortArray -> joinToString(fmt.separator, fmt.prefix, fmt.postfix)
        is IntArray -> joinToString(fmt.separator, fmt.prefix, fmt.postfix)
        is LongArray -> joinToString(fmt.separator, fmt.prefix, fmt.postfix)
        is FloatArray -> joinToString(fmt.separator, fmt.prefix, fmt.postfix)
        is DoubleArray -> joinToString(fmt.separator, fmt.prefix, fmt.postfix)
        is BooleanArray -> joinToString(fmt.separator, fmt.prefix, fmt.postfix)
        is Enum<*> -> "${javaClass.alteredName}.$name"
        else -> serialize()
    }
}

private val Class<*>.alteredName: String
    get() = if (cfg.classFormat.alterNames && packageName.startsWith("org.bukkit")) {
        simpleName.removePrefix("Craft")
    } else simpleName

private fun Any?.color(color: String) = "<color:$color>$this</color>"
