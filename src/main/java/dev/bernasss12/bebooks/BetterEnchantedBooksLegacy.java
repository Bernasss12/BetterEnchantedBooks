package dev.bernasss12.bebooks;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import dev.bernasss12.bebooks.util.ModConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class BetterEnchantedBooksLegacy {

    public static final Logger LOGGER = LogManager.getLogger("BEBooks");
    public static final ThreadLocal<Boolean> shouldShowEnchantmentMaxLevel = ThreadLocal.withInitial(() -> false);

    public static Map<Enchantment, List<ItemStack>> cachedApplicableEnchantments;

    public static void onInitializeClient() {
        cachedApplicableEnchantments = new WeakHashMap<>();
    }

    private static List<ItemStack> computeApplicableItems(Enchantment enchantment) {
        return ModConstants.DEFAULT_CHECKED_ITEMS_LIST.stream().filter(enchantment::isAcceptableItem).collect(Collectors.toList());
    }

    public static List<ItemStack> getApplicableItems(Enchantment enchantment) {
        return cachedApplicableEnchantments.computeIfAbsent(
                enchantment,
                BetterEnchantedBooksLegacy::computeApplicableItems
        );
    }
}
