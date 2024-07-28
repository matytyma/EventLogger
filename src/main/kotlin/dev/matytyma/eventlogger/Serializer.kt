package dev.matytyma.eventlogger

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

fun Any?.serialize(): String = when(this) {
    is Player -> "Player(name=$name, id=$uniqueId)"
    is Location -> "Location(world=${world.serialize()}, x=$x, y=$y, z=$z, yaw=$yaw, pitch=$pitch)"
    is World -> "World(name=$name)"
    else -> this.toString()
}
