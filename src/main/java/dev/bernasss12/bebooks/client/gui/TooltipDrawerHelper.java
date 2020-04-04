package dev.bernasss12.bebooks.client.gui;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class TooltipDrawerHelper {

    public static boolean isEnchantmentIconListMapPopulated = false;
    public static Map<Enchantment, List<ItemStack>> enchantmentIconListMap;

    public static void populateEnchantmentIconList() {
        TooltipDrawerHelper.enchantmentIconListMap = new HashMap<>();
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            List<ItemStack> list = new ArrayList<>();
            for (ItemStack icon : BEBooksConfig.checkedItemsList) {
                if (enchantment.isAcceptableItem(icon)) list.add(icon);
            }
            TooltipDrawerHelper.enchantmentIconListMap.put(enchantment, list);
        }
        isEnchantmentIconListMapPopulated = true;
    }

    public static List<ItemStack> get(Enchantment enchantment) {
        return enchantmentIconListMap.get(enchantment);
    }

    /*
    public static List<Identifier> addIfAbsent(List<Identifier> list, Identifier identifier) {
        if (!list.contains(identifier)) list.add(identifier);
        return list;
    }
    */

    public static class TooltipQueuedEntry {
        private int firstLine;
        private List<Enchantment> enchantments;

        public TooltipQueuedEntry(int firstLine, ListTag enchantments) {
            this.firstLine = firstLine;
            this.enchantments = new ArrayList<>();
            for (Tag enchantmentTag : enchantments) {
                Enchantment enchantment = Registry.ENCHANTMENT.get(new Identifier(((CompoundTag) enchantmentTag).getString("id")));
                this.enchantments.add(enchantment);
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
