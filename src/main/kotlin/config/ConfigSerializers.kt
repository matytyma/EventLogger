package dev.matytyma.eventlogger.config

import dev.matytyma.eventlogger.mm
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

object MiniMessageSerializer : KSerializer<Component> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MiniMessageSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Component = mm.deserialize(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Component) = encoder.encodeString(mm.serialize(value))
}

object ComponentLoggerAsPrefixSerializer : KSerializer<ComponentLogger> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ComponentLoggerAsPrefixSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ComponentLogger {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: ComponentLogger) = encoder.encodeString(value.name)
}
