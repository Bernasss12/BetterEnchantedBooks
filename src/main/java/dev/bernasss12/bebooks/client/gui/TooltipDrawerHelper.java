package dev.bernasss12.bebooks.client.gui;

import dev.bernasss12.bebooks.util.NBTUtils;
import dev.bernasss12.bebooks.util.NBTUtils.EnchantmentCompound;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TooltipDrawerHelper {

    public static boolean isEnchantmentIconListMapPopulated = false;
    public static int currentTooltipWidth = 0;
    public static Map<Enchantment, List<ItemStack>> enchantmentIconListMap;

    public static void populateEnchantmentIconList() {
        TooltipDrawerHelper.enchantmentIconListMap = new HashMap<>();
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            addEnchantmentToList(enchantment);
        }
        isEnchantmentIconListMapPopulated = true;
    }

    private static List<ItemStack> addEnchantmentToList(@NotNull Enchantment enchantment) {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack icon : ModConfig.checkedItemsList) {
            if (enchantment.isAcceptableItem(icon)) list.add(icon);
        }
        enchantmentIconListMap.put(enchantment, list);
        return list;
    }

    public static List<ItemStack> getAndComputeIfAbsent(@NotNull Enchantment enchantment) {
        if (enchantmentIconListMap.containsKey(enchantment)) return enchantmentIconListMap.get(enchantment);
        else return addEnchantmentToList(enchantment);
    }

    public static List<LiteralText> getSpacerLines(Enchantment enchantment, int tooltipWidth) {
        List<LiteralText> spacers = new ArrayList<>();
        int iconCount = enchantmentIconListMap.getOrDefault(enchantment, Collections.emptyList()).size();
        int iconsWidth = iconCount * 8;
        int extraLineCount = iconsWidth > 0 && tooltipWidth > 0 ? iconsWidth / tooltipWidth : 0;
        for (int i = 0; i < 1 + extraLineCount; i++) spacers.add(new LiteralText(" "));
        return spacers;
    }

    public static class TooltipQueuedEntry {
        private final int firstLine;
        private final List<Enchantment> enchantments;

        public TooltipQueuedEntry(int firstLine, NbtList enchantments) {
            this.firstLine = firstLine;
            this.enchantments = new ArrayList<>();
            for (NbtElement enchantmentTag : enchantments) {
                Enchantment enchantment = Registry.ENCHANTMENT.get(Identifier.tryParse(((NbtCompound) enchantmentTag).getString("id")));
                if (enchantment != null) {
                    this.enchantments.add(enchantment);
                }
            }
        }

        public int getFirstLine() {
            return firstLine;
        }

        public List<Enchantment> getList() {
            return enchantments;
        }
    }
}
