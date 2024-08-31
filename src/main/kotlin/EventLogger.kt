package dev.matytyma.eventlogger

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.file.TomlFileReader
import com.akuleshov7.ktoml.file.TomlFileWriter
import dev.matytyma.eventlogger.command.MainCommand
import dev.matytyma.minekraft.plugin.on
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var plugin: EventLogger

lateinit var cfg: Config

val mm: MiniMessage = MiniMessage.miniMessage()

var events: Set<LoggerData<*>> = emptySet()

class EventLogger : JavaPlugin() {
    lateinit var config: Config
    val listeners: MutableMap<LoggerData<*>, Listener> = mutableMapOf()

    fun loadConfig() {
        val file = File(dataFolder, "config.toml")
        if (file.exists()) {
            cfg = TomlFileReader.decodeFromFile(serializer(), file.absolutePath)
        } else {
            cfg = Config()
            TomlFileWriter().encodeToFile(serializer(), cfg, file.absolutePath)
        }
    }

    fun registerEvent(logger: LoggerData<*>) {
        listeners[logger] = plugin.on(logger.eventClass, EventPriority.MONITOR) {
            logger.logData(this)
        }
    }

    fun registerEvents() = events.forEach(::registerEvent)

    override fun onEnable() {
        plugin = this
        loadConfig()
        registerEvents()
        getCommand("el")?.apply {
            setExecutor(MainCommand)
            tabCompleter = MainCommand
        }
    }
}
