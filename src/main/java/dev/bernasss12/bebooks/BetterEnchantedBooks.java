package dev.bernasss12.bebooks;

import dev.bernasss12.bebooks.client.gui.ModConfig;
import dev.bernasss12.bebooks.client.gui.ModConstants;
import dev.bernasss12.bebooks.client.gui.TooltipDrawerHelper;
import dev.bernasss12.bebooks.util.NBTUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.WeakHashMap;

import static dev.bernasss12.bebooks.client.gui.ModConstants.DEFAULT_BOOK_STRIP_COLOR;

@Environment(EnvType.CLIENT)
public class BetterEnchantedBooks implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("BEBooks");

    private static Map<ItemStack, Integer> cachedColors;
    public static Map<ItemStack, TooltipDrawerHelper.TooltipQueuedEntry> cachedTooltipIcons;

    public static ThreadLocal<ItemStack> enchantedItemStack;
    public static ThreadLocal<Boolean> shouldShowEnchantmentMaxLevel;

    @Override
    public void onInitializeClient() {
        ModConstants.populateDefaultColorsMap();
        cachedColors = new WeakHashMap<>();
        cachedTooltipIcons = new WeakHashMap<>();
        enchantedItemStack = ThreadLocal.withInitial(() -> ItemStack.EMPTY);
        shouldShowEnchantmentMaxLevel = ThreadLocal.withInitial(() -> false);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? getColorFromEnchantmentList(stack) : -1, Items.ENCHANTED_BOOK);
    }

    public static int getColorFromEnchantmentList(ItemStack stack) {
        if (!ModConfig.doColorBooks) return DEFAULT_BOOK_STRIP_COLOR;

        Integer cached = cachedColors.get(stack);
        if (cached != null) return cached;

        Integer mapped = ModConfig.mappedEnchantmentColors.get(NBTUtils.getPriorityEnchantmentId(EnchantedBookItem.getEnchantmentTag(stack), ModConfig.colorPrioritySetting));
        if (mapped != null) {
            cachedColors.put(stack, mapped);
            return mapped;
        }

        cachedColors.put(stack, DEFAULT_BOOK_STRIP_COLOR);
        return DEFAULT_BOOK_STRIP_COLOR;
    }

    public static void clearCachedColors() {
        cachedColors.clear();
    }
}
