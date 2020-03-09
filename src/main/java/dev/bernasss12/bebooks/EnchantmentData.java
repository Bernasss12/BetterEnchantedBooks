package dev.bernasss12.bebooks;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EnchantmentData implements Comparable< EnchantmentData >{
    public String id;
    public String translatedName;
    public int lvl;
    public int orderIndex;

    EnchantmentData(String id, int lvl){
        this.id = id;
        this.lvl = lvl;
        this.orderIndex = 0;
        Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(id)).ifPresent((e) -> {
            this.translatedName = (e.getName(lvl)).asString();
        });;
    }

    EnchantmentData(CompoundTag tag){
         this(tag.getString("id"), tag.getShort("lvl"));
    }

    @Override
    public int compareTo(EnchantmentData enchantmentData) {
        return this.id.compareTo(enchantmentData.id);
    }
}
