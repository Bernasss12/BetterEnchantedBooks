package dev.bernasss12.bebooks;

import dev.bernasss12.bebooks.client.gui.config.BEBooksConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@Environment(EnvType.CLIENT)
public class BetterEnchantedBooks implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? getColorFromEnchantmentList(stack) : -1, Items.ENCHANTED_BOOK);
    }

    /* Get color associated with most valuable enchantment in the list enchantment. */
    public int getColorFromEnchantmentList(ItemStack stack) {
        if (!BEBooksConfig.COLOR_BOOKS || !BEBooksConfig.LOADED) return BEBooksConfig.DEFAULT_COLOR;
        if (stack.isItemEqual(new ItemStack(Items.ENCHANTED_BOOK)) && stack.hasEnchantments()) {
            ListTag enchantments = EnchantedBookItem.getEnchantmentTag(stack);
            if (enchantments.size() == 1)
                return BEBooksConfig.ENCHANTMENT_COLORS.get(enchantments.getCompound(0).getString("id"));
        }
        return BEBooksConfig.DEFAULT_COLOR;
    }
}
