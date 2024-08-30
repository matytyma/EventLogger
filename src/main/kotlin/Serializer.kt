package dev.matytyma.eventlogger

import dev.matytyma.eventlogger.Config.arrayPostfix
import dev.matytyma.eventlogger.Config.arrayPrefix
import dev.matytyma.eventlogger.Config.arraySeparator
import dev.matytyma.eventlogger.Config.classPostfix
import dev.matytyma.eventlogger.Config.classPrefix
import dev.matytyma.eventlogger.Config.classSeparator
import dev.matytyma.eventlogger.Config.fieldSeparator
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

const val GREEN = "#a9dc76"
const val GRAY = "#939293"
const val WHITE = "#fcfcfa"
const val RED = "#ff6188"
const val PURPLE = "#ab9df2"
const val YELLOW = "#ffd866"
const val CYAN = "#78dce8"

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

private fun Any.formatClass(properties: List<Pair<String, Any?>>): String = "<color:$GREEN>${javaClass.alteredName}</color>${
    properties.joinToString(
        separator = classSeparator, prefix = classPrefix, postfix = classPostfix
    ) { (name: String, value: Any?) -> "$name$fieldSeparator${value.formatValue()}" }
}"

private fun Any?.formatValue(): String = when (this) {
    is Number -> toString()
    is CharSequence -> toString()
    is Boolean -> toString()
    is Collection<*> -> joinToString(arraySeparator, arrayPrefix, arrayPostfix) { it.formatValue() }
    is Array<*> -> joinToString(arraySeparator, arrayPrefix, arrayPostfix) { it.formatValue() }
    is ByteArray -> joinToString(arraySeparator, arrayPrefix, arrayPostfix)
    is CharArray -> joinToString(arraySeparator, arrayPrefix, arrayPostfix)
    is ShortArray -> joinToString(arraySeparator, arrayPrefix, arrayPostfix)
    is IntArray -> joinToString(arraySeparator, arrayPrefix, arrayPostfix)
    is LongArray -> joinToString(arraySeparator, arrayPrefix, arrayPostfix)
    is FloatArray -> joinToString(arraySeparator, arrayPrefix, arrayPostfix)
    is DoubleArray -> joinToString(arraySeparator, arrayPrefix, arrayPostfix)
    is BooleanArray -> joinToString(arraySeparator, arrayPrefix, arrayPostfix)
    is Enum<*> -> "${javaClass.alteredName}.$name"
    else -> serialize()
}

private val Class<*>.alteredName: String
    get() = if (Config.alterClassNames && packageName.startsWith("org.bukkit")) {
        simpleName.removePrefix("Craft")
    } else simpleName
