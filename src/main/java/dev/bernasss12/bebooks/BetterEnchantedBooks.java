package dev.bernasss12.bebooks;

import dev.bernasss12.bebooks.client.gui.ModConfig;
import dev.bernasss12.bebooks.client.gui.ModConfig.EnchantmentData;
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

    public static final ThreadLocal<ItemStack> enchantedItemStack = ThreadLocal.withInitial(() -> ItemStack.EMPTY);
    public static final ThreadLocal<Boolean> shouldShowEnchantmentMaxLevel = ThreadLocal.withInitial(() -> false);

    @Override
    public void onInitializeClient() {
        cachedColors = new WeakHashMap<>();
        cachedTooltipIcons = new WeakHashMap<>();

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? getColorFromEnchantmentList(stack) : -1, Items.ENCHANTED_BOOK);
    }

    public static int getColorFromEnchantmentList(ItemStack stack) {
        if (!ModConfig.doColorBooks) return DEFAULT_BOOK_STRIP_COLOR;

        Integer cached = cachedColors.get(stack);
        if (cached != null) return cached;

        EnchantmentData data = ModConfig.enchantmentDataMap.get(NBTUtils.getPriorityEnchantmentId(EnchantedBookItem.getEnchantmentNbt(stack), ModConfig.colorPrioritySetting));
        if (data != null) {
            cachedColors.put(stack, data.color);
            return data.color;
        }

        cachedColors.put(stack, DEFAULT_BOOK_STRIP_COLOR);
        return DEFAULT_BOOK_STRIP_COLOR;
    }

    public static void clearCachedColors() {
        cachedColors.clear();
    }
}
