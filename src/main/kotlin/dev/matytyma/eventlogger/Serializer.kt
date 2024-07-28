package dev.matytyma.eventlogger

fun Any?.serialize() = when(this) {
    else -> this.toString()
}
