package dev.bernasss12.bebooks.util

import dev.bernasss12.bebooks.client.gui.ModConfigLegacy
import dev.bernasss12.bebooks.config.ModConfig;
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_BOOK_STRIP_COLOR
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack

object BookColorManager {
    private val cache = hashMapOf<ItemStack, Int>()

    val itemColorProvider = ItemColorProvider { stack: ItemStack?, tintIndex: Int ->
        if (!ModConfig.colorBooks || stack == null) return@ItemColorProvider DEFAULT_BOOK_STRIP_COLOR
        if (tintIndex != 1) return@ItemColorProvider 0xffffffff.toInt()

        cache.getOrPut(stack) {
            ModConfigLegacy.enchantmentDataMap[
                NBTUtils.getPriorityEnchantmentId(
                    EnchantedBookItem.getEnchantmentNbt(stack),
                    ModConfig.colorMode
                )
            ]?.color ?: DEFAULT_BOOK_STRIP_COLOR
        }
    }

    @JvmStatic // TODO remove after not used in java
    fun clear() = cache.clear()
}