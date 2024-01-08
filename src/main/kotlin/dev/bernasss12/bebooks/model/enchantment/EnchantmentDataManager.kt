package dev.bernasss12.bebooks.model.enchantment

import dev.bernasss12.bebooks.model.color.Color
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_BOOK_STRIP_COLOR
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_ENCHANTMENT_COLORS
import kotlinx.serialization.Serializable
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

/*
    The "version 2" spec of the enchantment data config will be as follows:
    - Object will be composed of "version" value as well as "enchantments" object.
        - "version" is pretty self-explanatory, this will help any future changes support older ones until deprecated.
        - "enchantments" will be an array of objects that will have all the necessary data.
    - Only enchantments with a meaningful order or color value will be saved.
 */

object EnchantmentDataManager {
    private val cache = hashMapOf<Identifier, EnchantmentData>()
    private var loaded = false

    fun getData(key: Identifier): EnchantmentData {
        return cache.getOrPut(key) {
            EnchantmentData(
                identifier = key,
                priority = -1,
                color = getDefaultColorForId(key)
            )
        }
    }

    fun getData(key: String): EnchantmentData {
        val identifier = Identifier.tryParse(key)
        return if (identifier != null) {
            getData(identifier)
        } else {
            throw IllegalArgumentException("Invalid identifier: $key")
        }
    }

    private fun getDefaultColorForId(identifier: Identifier): Color {
        val enchantment = Registries.ENCHANTMENT.get(identifier) ?: return DEFAULT_BOOK_STRIP_COLOR
        return DEFAULT_ENCHANTMENT_COLORS[enchantment] ?: DEFAULT_BOOK_STRIP_COLOR
    }

    fun save() {
        //
    }

    fun load() {
        // TODO
        loaded = true
    }

    @Serializable
    data class SavedData(val version: Int, val enchantments: List<EnchantmentData>)
}