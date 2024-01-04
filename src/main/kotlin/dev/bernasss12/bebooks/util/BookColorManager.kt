package dev.bernasss12.bebooks.util

import dev.bernasss12.bebooks.client.gui.ModConfig
import dev.bernasss12.bebooks.client.gui.ModConfig.doColorBooks
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_BOOK_STRIP_COLOR
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack

object BookColorManager {
    private val cache = hashMapOf<ItemStack, Int>()

    val itemColorProvider = ItemColorProvider { stack: ItemStack?, tintIndex: Int ->
        if (!doColorBooks || stack == null) return@ItemColorProvider DEFAULT_BOOK_STRIP_COLOR
        if (tintIndex != 1) return@ItemColorProvider 0xffffffff.toInt()

        cache.getOrPut(stack) {
            ModConfig.enchantmentDataMap[
                NBTUtils.getPriorityEnchantmentId(
                    EnchantedBookItem.getEnchantmentNbt(stack),
                    ModConfig.colorPrioritySetting
                )
            ]?.color ?: DEFAULT_BOOK_STRIP_COLOR
        }
    }

    @JvmStatic // TODO remove after not used in java
    fun clear() = cache.clear()
}