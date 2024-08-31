package dev.matytyma.eventlogger

import com.akuleshov7.ktoml.TomlIndentation
import com.akuleshov7.ktoml.TomlOutputConfig
import com.akuleshov7.ktoml.file.TomlFileReader
import com.akuleshov7.ktoml.file.TomlFileWriter
import dev.matytyma.eventlogger.command.MainCommand
import dev.matytyma.eventlogger.config.Config
import dev.matytyma.eventlogger.config.Theme
import dev.matytyma.minekraft.plugin.on
import kotlinx.serialization.serializer
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var plugin: EventLogger

lateinit var cfg: Config

lateinit var theme: Theme

val mm: MiniMessage = MiniMessage.miniMessage()

var events: Set<LoggerData<*>> = emptySet()

class EventLogger : JavaPlugin() {
    private val listeners: MutableMap<LoggerData<*>, Listener> = mutableMapOf()

    private inline fun <reified T> loadResource(fileName: String, default: () -> T): T {
        val file = File(dataFolder, fileName)
        return if (file.exists()) {
            TomlFileReader.decodeFromFile(serializer(), file.absolutePath)
        } else {
            val resource = default()
            saveResource(file.absolutePath, resource)
            return resource
        }
    }

    private fun saveResource(filePath: String, data: Any?) {
        TomlFileWriter(outputConfig = TomlOutputConfig(TomlIndentation.NONE)).encodeToFile(serializer(), data, filePath)
    }

    fun registerEvent(logger: LoggerData<*>) {
        listeners[logger] = plugin.on(logger.eventClass, EventPriority.MONITOR) {
            logger.logData(this)
        }
    }

    fun registerEvents() = events.forEach(::registerEvent)

    override fun onEnable() {
        plugin = this
        cfg = loadResource("config.toml") { Config() }
        theme = loadResource("theme.toml") { Theme() }
        registerEvents()
        getCommand("el")?.apply {
            setExecutor(MainCommand)
            tabCompleter = MainCommand
        }
    }
}
