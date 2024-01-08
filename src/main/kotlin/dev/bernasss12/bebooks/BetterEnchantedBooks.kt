package dev.bernasss12.bebooks

import dev.bernasss12.bebooks.config.ModConfig.load
import dev.bernasss12.bebooks.config.ModConfig.save
import dev.bernasss12.bebooks.model.color.BookColorManager.itemColorProvider
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.item.Items

object BetterEnchantedBooks {
    fun init() {
        BetterEnchantedBooksLegacy.onInitializeClient()

        ColorProviderRegistry.ITEM.register(itemColorProvider, Items.ENCHANTED_BOOK)
    }

    @JvmStatic //TODO remove when TitleScreen MIXIN is converted to kotlin.
    fun onTitleScreenLoaded() {
//        if (!ModConfigLegacy.configsFirstLoaded) {
        //TooltipDrawerHelper.populateEnchantmentIconList();
        load()
        save()
//            ModConfigLegacy.loadAndPopulateConfig()
//            ModConfigLegacy.saveConfig()
//        }
    }
}