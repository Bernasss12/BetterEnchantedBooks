package dev.bernasss12.bebooks;

import dev.bernasss12.bebooks.client.gui.config.BEBooksConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;

@Environment(EnvType.CLIENT)
public class EnchantmentData implements Comparable<EnchantmentData> {

    public String translatedName;
    public int orderIndex;
    public int color;

    public String id = "";
    public int lvl;

    public EnchantmentData(String translatedName, int index, int color) {
        this.orderIndex = index;
        this.color = color;
        this.translatedName = translatedName;
    }

    //TODO take recent changes into account
    public EnchantmentData(CompoundTag tag) {
        EnchantmentData enchantmentData = BEBooksConfig.ENCHANTMENT_DATA.get(tag.getString("id"));
        new EnchantmentData(enchantmentData);
    }

    public EnchantmentData(EnchantmentData enchantmentData) {
        new EnchantmentData(enchantmentData.translatedName, enchantmentData.orderIndex, enchantmentData.color);
    }

    @Override
    public int compareTo(EnchantmentData enchantmentData) {
        return orderIndex;
    }
}
