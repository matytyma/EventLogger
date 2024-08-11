package dev.matytyma.eventlogger

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

private fun Any.formatClass(properties: List<Pair<String, Any?>>) = "${javaClass.simpleName}${
    properties.joinToString(
        separator = ", ", prefix = "(", postfix = ")"
    ) { (name, value) -> "$name=${value.formatValue()}" }
}"

private fun Any?.formatValue(): String = when (this) {
    is Number -> toString()
    is CharSequence -> toString()
    is Boolean -> toString()
    is Collection<*> -> joinToString(", ", "[", "]") { it.formatValue() }
    is Array<*> -> joinToString(", ", "[", "]") { it.formatValue() }
    is ByteArray -> joinToString(", ", "[", "]")
    is CharArray -> joinToString(", ", "[", "]")
    is ShortArray -> joinToString(", ", "[", "]")
    is IntArray -> joinToString(", ", "[", "]")
    is LongArray -> joinToString(", ", "[", "]")
    is FloatArray -> joinToString(", ", "[", "]")
    is DoubleArray -> joinToString(", ", "[", "]")
    is BooleanArray -> joinToString(", ", "[", "]")
    is Enum<*> -> "${javaClass.simpleName}.$name"
    else -> serialize()
}
