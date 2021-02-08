package dev.bernasss12.bebooks.client.gui;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModConstants {
    protected static final int SETTINGS_VERSION = 2;

    protected static final boolean DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL = false;

    protected static final List<ItemStack> DEFAULT_CHECKED_ITEMS_LIST = Arrays.asList(
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
            new ItemStack(Items.SHIELD)
    );

    // Default enchantment colors as suggested by twusya on https://www.curseforge.com/minecraft/mc-mods/better-enchanted-books#c47
    public static final Map<Enchantment, Integer> DEFAULT_ENCHANTMENT_COLORS = new HashMap<>();

    public static void populateDefaultColorsMap() {
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.AQUA_AFFINITY, 0x6e7af7);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.BANE_OF_ARTHROPODS, 0x0f5160);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.BLAST_PROTECTION, 0x442e62);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.CHANNELING, 0xaef5ff);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.BINDING_CURSE, 0x274d1e);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.VANISHING_CURSE, 0x171220);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.DEPTH_STRIDER, 0x9cdbff);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.EFFICIENCY, 0xfff164);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.FEATHER_FALLING, 0xfff0d1);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.FIRE_ASPECT, 0xff7516);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.FIRE_PROTECTION, 0xc4aaa5);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.FLAME, 0xff7516);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.FORTUNE, 0xffb65b);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.FROST_WALKER, 0x90b4ff);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.IMPALING, 0xc5133a);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.INFINITY, 0x7b5be7);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.KNOCKBACK, 0x605b60);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.LOOTING, 0xffb65b);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.LOYALTY, 0x6ec4b1);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.LUCK_OF_THE_SEA, 0x4be850);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.LURE, 0xff0000);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.MENDING, 0xcaff61);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.MULTISHOT, 0xffb301);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.PIERCING, 0x337b50);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.POWER, 0xc5133a);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.PROJECTILE_PROTECTION, 0xcccdd5);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.PROTECTION, 0xa5afc4);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.PUNCH, 0x605b60);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.QUICK_CHARGE, 0xff0000);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.RESPIRATION, 0x7ad5ff);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.RIPTIDE, 0xaccff1);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.SHARPNESS, 0xc5133a);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.SILK_TOUCH, 0xffffff);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.SMITE, 0xbf5f2e);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.SOUL_SPEED, 0x41342c);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.SWEEPING, 0xffb301);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.THORNS, 0x560d0b);
        DEFAULT_ENCHANTMENT_COLORS.put(Enchantments.UNBREAKING, 0x5c3350);
    }

    protected static final ModConfig.TooltipSetting DEFAULT_TOOLTIP_SETTING = ModConfig.TooltipSetting.ON_SHIFT;
    protected static final ModConfig.SortingSetting DEFAULT_SORTING_SETTING = ModConfig.SortingSetting.ALPHABETICALLY;
    protected static final boolean DEFAULT_KEEP_CURSES_BELOW = true;
    protected static final boolean DEFAULT_COLOR_BOOKS = true;
    protected static final boolean DEFAULT_CURSE_COLOR_OVERRIDE = true;
    protected static final ModConfig.SortingSetting DEFAULT_COLOR_PRIORITY_SETTING = ModConfig.SortingSetting.ALPHABETICALLY;
    public static final int DEFAULT_BOOK_STRIP_COLOR = 0xc5133a;
    protected static final Boolean DEFAULT_GLINT_SETTING = false;

    public static final String MODID = "bebooks";
}
