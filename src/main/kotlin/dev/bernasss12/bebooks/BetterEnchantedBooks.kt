package dev.bernasss12.bebooks

import dev.bernasss12.bebooks.config.ModConfig
import dev.bernasss12.bebooks.model.color.BookColorManager.itemColorProvider
import dev.bernasss12.bebooks.model.enchantment.EnchantmentDataManager
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.item.Items

object BetterEnchantedBooks {
    @Suppress("Unused")
    fun init() {
        BetterEnchantedBooksLegacy.onInitializeClient()

        ColorProviderRegistry.ITEM.register(itemColorProvider, Items.ENCHANTED_BOOK)
    }

    @JvmStatic //TODO remove when TitleScreen MIXIN is converted to kotlin.
    fun onTitleScreenLoaded() {
        ModConfig.load()
        EnchantmentDataManager.load()
    }
}