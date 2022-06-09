package dev.bernasss12.bebooks.client.gui;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Map;

public class ModConstants {
    protected static final int SETTINGS_VERSION = 2;

    public static final List<ItemStack> DEFAULT_CHECKED_ITEMS_LIST = List.of(
        new ItemStack(Items.DIAMOND_SWORD),
        new ItemStack(Items.DIAMOND_PICKAXE),
        new ItemStack(Items.DIAMOND_AXE),
        new ItemStack(Items.DIAMOND_SHOVEL),
        new ItemStack(Items.DIAMOND_HOE),
        new ItemStack(Items.BOW),
        new ItemStack(Items.CROSSBOW),
        new ItemStack(Items.FISHING_ROD),
        new ItemStack(Items.TRIDENT),
        new ItemStack(Items.DIAMOND_HELMET),
        new ItemStack(Items.DIAMOND_CHESTPLATE),
        new ItemStack(Items.DIAMOND_LEGGINGS),
        new ItemStack(Items.DIAMOND_BOOTS),
        new ItemStack(Items.ELYTRA),
        new ItemStack(Items.SHIELD),
        new ItemStack(Items.SHEARS),
        new ItemStack(Items.CARROT_ON_A_STICK),
        new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK),
        new ItemStack(Items.FLINT_AND_STEEL)
    );

    // Default enchantment colors as suggested by twusya on https://www.curseforge.com/minecraft/mc-mods/better-enchanted-books#c47
    public static final Map<Enchantment, Integer> DEFAULT_ENCHANTMENT_COLORS = Map.ofEntries(
        Map.entry(Enchantments.AQUA_AFFINITY, 0x6e7af7),
        Map.entry(Enchantments.BANE_OF_ARTHROPODS, 0x0f5160),
        Map.entry(Enchantments.BLAST_PROTECTION, 0x442e62),
        Map.entry(Enchantments.CHANNELING, 0xaef5ff),
        Map.entry(Enchantments.BINDING_CURSE, 0x274d1e),
        Map.entry(Enchantments.VANISHING_CURSE, 0x171220),
        Map.entry(Enchantments.DEPTH_STRIDER, 0x9cdbff),
        Map.entry(Enchantments.EFFICIENCY, 0xfff164),
        Map.entry(Enchantments.FEATHER_FALLING, 0xfff0d1),
        Map.entry(Enchantments.FIRE_ASPECT, 0xff7516),
        Map.entry(Enchantments.FIRE_PROTECTION, 0xc4aaa5),
        Map.entry(Enchantments.FLAME, 0xff7516),
        Map.entry(Enchantments.FORTUNE, 0xffb65b),
        Map.entry(Enchantments.FROST_WALKER, 0x90b4ff),
        Map.entry(Enchantments.IMPALING, 0xc5133a),
        Map.entry(Enchantments.INFINITY, 0x7b5be7),
        Map.entry(Enchantments.KNOCKBACK, 0x605b60),
        Map.entry(Enchantments.LOOTING, 0xffb65b),
        Map.entry(Enchantments.LOYALTY, 0x6ec4b1),
        Map.entry(Enchantments.LUCK_OF_THE_SEA, 0x4be850),
        Map.entry(Enchantments.LURE, 0xff0000),
        Map.entry(Enchantments.MENDING, 0xcaff61),
        Map.entry(Enchantments.MULTISHOT, 0xffb301),
        Map.entry(Enchantments.PIERCING, 0x337b50),
        Map.entry(Enchantments.POWER, 0xc5133a),
        Map.entry(Enchantments.PROJECTILE_PROTECTION, 0xcccdd5),
        Map.entry(Enchantments.PROTECTION, 0xa5afc4),
        Map.entry(Enchantments.PUNCH, 0x605b60),
        Map.entry(Enchantments.QUICK_CHARGE, 0xff0000),
        Map.entry(Enchantments.RESPIRATION, 0x7ad5ff),
        Map.entry(Enchantments.RIPTIDE, 0xaccff1),
        Map.entry(Enchantments.SHARPNESS, 0xc5133a),
        Map.entry(Enchantments.SILK_TOUCH, 0xffffff),
        Map.entry(Enchantments.SMITE, 0xbf5f2e),
        Map.entry(Enchantments.SOUL_SPEED, 0x41342c),
        Map.entry(Enchantments.SWEEPING, 0xffb301),
        Map.entry(Enchantments.THORNS, 0x560d0b),
        Map.entry(Enchantments.UNBREAKING, 0x5c3350)
    );

    public static final boolean DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL = false;
    public static final ModConfig.TooltipSetting DEFAULT_TOOLTIP_SETTING = ModConfig.TooltipSetting.ON_SHIFT;
    public static final ModConfig.SortingSetting DEFAULT_SORTING_SETTING = ModConfig.SortingSetting.ALPHABETICALLY;
    public static final boolean DEFAULT_KEEP_CURSES_BELOW = true;
    public static final boolean DEFAULT_COLOR_BOOKS = true;
    public static final boolean DEFAULT_CURSE_COLOR_OVERRIDE = true;
    public static final ModConfig.SortingSetting DEFAULT_COLOR_PRIORITY_SETTING = ModConfig.SortingSetting.ALPHABETICALLY;
    public static final int DEFAULT_BOOK_STRIP_COLOR = 0xc5133a;
    public static final Boolean DEFAULT_GLINT_SETTING = false;
}
