package dev.bernasss12.bebooks

import dev.bernasss12.bebooks.util.BookColorManager.itemColorProvider
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.item.Items

object BetterEnchantedBooks {
    fun init() {
        BetterEnchantedBooksLegacy.onInitializeClient()

        ColorProviderRegistry.ITEM.register(itemColorProvider, Items.ENCHANTED_BOOK)
    }
}