package dev.bernasss12.bebooks.model.color

import dev.bernasss12.bebooks.config.ModConfig
import dev.bernasss12.bebooks.util.Util.isInt
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Color.ColorSerializer::class)
class Color(val rgb: Int) {
    object ColorSerializer : KSerializer<Color> {
        override val descriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Color {
            val value = decoder.decodeString()
            return when {
                value.startsWith("rgb") -> ColorSavingMode.RGB_VALUES.deserialize(value)
                value.startsWith("#") -> ColorSavingMode.HEXADECIMAL.deserialize(value)
                value.isInt() -> ColorSavingMode.INTEGER.deserialize(value)
                else -> throw SerializationException("[$value] is not a valid color string.")
            }
        }

        override fun serialize(encoder: Encoder, value: Color) {
            encoder.encodeString(
                ModConfig.colorSavingMode.serialize(value)
            )
        }
    }

    override fun toString(): String {
        return ColorSavingMode.HEXADECIMAL.serialize(this)
    }
}

