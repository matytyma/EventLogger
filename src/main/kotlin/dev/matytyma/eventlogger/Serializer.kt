package dev.matytyma.eventlogger

import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player

fun Any?.serialize(): String = when (this) {
    is Block -> "Block(position=[$x, $y, $z], type=${type.serialize()}, data=$blockData)"
    is Location -> "Location(world=${world.serialize()}, x=$x, y=$y, z=$z, yaw=$yaw, pitch=$pitch)"
    is Material -> "Material.$name"
    is Player -> "Player(name=$name, id=$uniqueId)"
    is World -> "World(name=$name)"
    else -> this.toString()
}
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
