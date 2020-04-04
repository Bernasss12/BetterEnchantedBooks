package dev.bernasss12.bebooks;

import dev.bernasss12.bebooks.client.gui.BEBooksConfig;
import dev.bernasss12.bebooks.client.gui.TooltipDrawerHelper;
import dev.bernasss12.bebooks.util.NBTUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class BetterEnchantedBooks implements ClientModInitializer {

    public static final String MODID = "bebooks";

    private static Map<ItemStack, Integer> cachedColors;
    public static Map<ItemStack, TooltipDrawerHelper.TooltipQueuedEntry> cachedTooltipIcons;

    public static ThreadLocal<ItemStack> enchantedItemStack;

    @Override
    public void onInitializeClient() {
        cachedColors = new HashMap<>();
        cachedTooltipIcons = new HashMap<>();
        enchantedItemStack = new ThreadLocal<>();
        enchantedItemStack.set(ItemStack.EMPTY);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? getColorFromEnchantmentList(stack) : -1, Items.ENCHANTED_BOOK);
    }

    public static int getColorFromEnchantmentList(ItemStack stack) {
        if (!BEBooksConfig.doColorBooks) return BEBooksConfig.defaultBookStripColor;
        if (cachedColors.containsKey(stack)) return cachedColors.get(stack);
        else {
            int color = BEBooksConfig.defaultBookStripColor;
            if (stack.isItemEqual(new ItemStack(Items.ENCHANTED_BOOK))) {
                try {
                    color = BEBooksConfig.mappedEnchantmentColors.get(NBTUtils.getPriorityEnchantmentId(EnchantedBookItem.getEnchantmentTag(stack), BEBooksConfig.doColorBasedOnAlphabeticalOrder));
                    cachedColors.putIfAbsent(stack, color);
                } catch (Exception e) {
                    return color;
                }
            }
            return color;
        }
    }

    public static void clearCachedColors() {
        if (!cachedColors.isEmpty()) cachedColors.clear();
    }
}
