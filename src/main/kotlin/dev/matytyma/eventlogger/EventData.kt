package dev.matytyma.eventlogger

import org.bukkit.event.Event

class EventData<T : Event>(val eventClass: Class<T>, val logFunction: (T) -> Unit)
