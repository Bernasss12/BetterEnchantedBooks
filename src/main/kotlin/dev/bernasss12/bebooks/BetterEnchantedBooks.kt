package dev.bernasss12.bebooks

import dev.bernasss12.bebooks.config.ModConfig
import dev.bernasss12.bebooks.manage.BookColorManager.itemColorProvider
import dev.bernasss12.bebooks.manage.EnchantmentDataManager
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.item.Items
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Environment(EnvType.CLIENT)
object BetterEnchantedBooks {
    @Suppress("Unused")
    fun init() {
        ColorProviderRegistry.ITEM.register(itemColorProvider, Items.ENCHANTED_BOOK)
    }

    val LOGGER: Logger = LogManager.getLogger("BEBooks")

    @JvmStatic
    fun onTitleScreenLoaded() {
        ModConfig.load()
        EnchantmentDataManager.load()
    }
}