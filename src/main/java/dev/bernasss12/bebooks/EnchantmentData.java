package dev.bernasss12.bebooks;

import net.minecraft.nbt.CompoundTag;

public class EnchantmentData implements Comparable< EnchantmentData >{
    public String id;
    public int lvl;
    public int weight;
    public int color;

    EnchantmentData(String id, int lvl){
        this.id = id;
        this.lvl = lvl;
        this.weight = 0;
        switch (id){
            case "minecraft:mending":
                this.color = 0x11ef5e;
                break;
            case "minecraft:fortune":
                this.color = 0xfe115e;
                break;
            case "minecraft:silk_touch":
                this.color = 0x15ff15;
                break;
            default:
                this.color = 0x5e0000;
                break;
        }
    }

    EnchantmentData(CompoundTag tag){
         this(tag.getString("id"), tag.getShort("lvl"));
    }

    @Override
    public int compareTo(EnchantmentData enchantmentData) {
        return this.id.compareTo(enchantmentData.id);
    }
}
