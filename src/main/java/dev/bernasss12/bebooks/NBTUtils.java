package dev.bernasss12.bebooks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.List;

public final class NBTUtils {
    private NBTUtils(){}

    /* Turn a ListTag of enchantments into an ArrayList of enchantment data */
    public static List<EnchantmentData> getEnchantmentData(ListTag listTag){
        List<EnchantmentData> enchantmentList = new ArrayList<>();
        for(int i = 0; i < listTag.size(); i++){
            enchantmentList.add(new EnchantmentData(listTag.getCompound(i)));
        }
        return enchantmentList;
    }

    /* Turn an ArrayList of enchantment data into a ListTag of enchantments */
    public static ListTag getEnchantmentTag(List<EnchantmentData> enchantmentList){
        ListTag listTag = new ListTag();
        for(int i = 0; i < enchantmentList.size(); i++){
            CompoundTag enchantmentTag = new CompoundTag();
            enchantmentTag.putString("id", String.valueOf(enchantmentList.get(i).id));
            enchantmentTag.putShort("lvl", (short)enchantmentList.get(i).lvl);
            listTag.add(enchantmentTag);
        }
        return listTag;
    }
}
