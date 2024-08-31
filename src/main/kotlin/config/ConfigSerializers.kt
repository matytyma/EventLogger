package dev.matytyma.eventlogger.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

object ComponentLoggerAsPrefixSerializer : KSerializer<ComponentLogger> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("ComponentLoggerAsPrefixSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ComponentLogger = ComponentLogger.logger(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: ComponentLogger) = encoder.encodeString(value.name)
}
