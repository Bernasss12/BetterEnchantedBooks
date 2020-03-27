package dev.bernasss12.bebooks.client.gui;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class TooltipDrawerHelper {

    public static boolean isEnchantmentIconListMapPopulated = false;
    public static Map<Enchantment, List<IconIdentifier>> enchantmentIconListMap;

    public static final List<IconIdentifier> defaultIdentifierList = Arrays.asList(
            new IconIdentifier(new ItemStack(Items.DIAMOND_SWORD), new Identifier("textures/item/diamond_sword.png")),
            new IconIdentifier(new ItemStack(Items.DIAMOND_PICKAXE), new Identifier("textures/item/diamond_pickaxe.png")),
            new IconIdentifier(new ItemStack(Items.DIAMOND_AXE), new Identifier("textures/item/diamond_axe.png")),
            new IconIdentifier(new ItemStack(Items.DIAMOND_SHOVEL), new Identifier("textures/item/diamond_shovel.png")),
            new IconIdentifier(new ItemStack(Items.DIAMOND_HOE), new Identifier("textures/item/diamond_hoe.png")),
            new IconIdentifier(new ItemStack(Items.BOW), new Identifier("textures/item/bow.png")),
            new IconIdentifier(new ItemStack(Items.CROSSBOW), new Identifier("textures/item/crossbow_standby.png")),
            new IconIdentifier(new ItemStack(Items.FISHING_ROD), new Identifier("textures/item/fishing_rod.png")),
            new IconIdentifier(new ItemStack(Items.TRIDENT), new Identifier("textures/item/trident.png")),
            new IconIdentifier(new ItemStack(Items.DIAMOND_HELMET), new Identifier("textures/item/diamond_helmet.png")),
            new IconIdentifier(new ItemStack(Items.DIAMOND_CHESTPLATE), new Identifier("textures/item/diamond_chestplate.png")),
            new IconIdentifier(new ItemStack(Items.DIAMOND_LEGGINGS), new Identifier("textures/item/diamond_leggings.png")),
            new IconIdentifier(new ItemStack(Items.DIAMOND_BOOTS), new Identifier("textures/item/diamond_boots.png")),
            new IconIdentifier(new ItemStack(Items.ELYTRA), new Identifier("textures/item/elytra.png"))
    );

    public static void populateEnchantmentIconList() {
        TooltipDrawerHelper.enchantmentIconListMap = new HashMap<>();
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            List<IconIdentifier> list = new ArrayList<>();
            for (IconIdentifier icon : defaultIdentifierList) {
                if (enchantment.isAcceptableItem(icon.testItem)) list.add(icon);
            }
            TooltipDrawerHelper.enchantmentIconListMap.put(enchantment, list);
        }
        isEnchantmentIconListMapPopulated = true;
    }

    public static List<IconIdentifier> get(Enchantment enchantment) {
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
        private List<List<IconIdentifier>> icons;

        public TooltipQueuedEntry(int firstLine, ListTag enchantments) {
            this.firstLine = firstLine;
            icons = new ArrayList<>();
            for (Tag enchantmentTag : enchantments) {
                Enchantment enchantment = Registry.ENCHANTMENT.get(new Identifier(((CompoundTag) enchantmentTag).getString("id")));
                icons.add(enchantmentIconListMap.get(enchantment));
            }
        }

        public int getFirstLine() {
            return firstLine;
        }

        public List<List<IconIdentifier>> getList() {
            return icons;
        }
    }

    public static class IconIdentifier {
        private ItemStack testItem;
        private Identifier textureIdentifier;

        IconIdentifier(ItemStack testItem, Identifier identifier) {
            this.testItem = testItem;
            this.textureIdentifier = identifier;
        }

        public ItemStack getTestItem() {
            return testItem;
        }

        public Identifier getTextureIdentifier() {
            return textureIdentifier;
        }
    }
}
