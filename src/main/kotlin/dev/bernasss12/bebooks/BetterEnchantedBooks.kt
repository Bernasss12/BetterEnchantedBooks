package dev.bernasss12.bebooks

import dev.bernasss12.bebooks.config.ModConfig
import dev.bernasss12.bebooks.config.ModConfig.showMaxEnchantmentLevel
import dev.bernasss12.bebooks.model.color.BookColorManager.itemColorProvider
import dev.bernasss12.bebooks.model.enchantment.EnchantmentDataManager
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Environment(EnvType.CLIENT)
object BetterEnchantedBooks {
    @Suppress("Unused")
    fun init() {
        BetterEnchantedBooksLegacy.onInitializeClient()
        ColorProviderRegistry.ITEM.register(itemColorProvider, Items.ENCHANTED_BOOK)
    }

    val LOGGER: Logger = LogManager.getLogger("BEBooks")
    private val currentItemstack = ThreadLocal.withInitial { ItemStack.EMPTY }

    @JvmStatic
    fun onTitleScreenLoaded() {
        ModConfig.load()
        EnchantmentDataManager.load()
    }

    @JvmStatic
    fun setItemstack(stack: ItemStack) {
        currentItemstack.set(stack)
    }

    @JvmStatic
    fun getItemstack(): ItemStack = currentItemstack.get()
}