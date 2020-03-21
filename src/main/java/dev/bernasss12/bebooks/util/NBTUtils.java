package dev.bernasss12.bebooks.util;

import dev.bernasss12.bebooks.EnchantmentData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class NBTUtils {
    private NBTUtils() {
    }


    //TODO fix all this, it's utterly broken

    /* Turn a ListTag of enchantments into an ArrayList of enchantment data */
    public static List<EnchantmentData> getEnchantmentData(ListTag listTag) {
        List<EnchantmentData> enchantmentList = new ArrayList<>();
        for (int i = 0; i < listTag.size(); i++) {
            enchantmentList.add(new EnchantmentData(listTag.getCompound(i)));
        }
        return enchantmentList;
    }

    /* Turn an ArrayList of enchantment data into a ListTag of enchantments */
    public static ListTag getEnchantmentListTag(List<EnchantmentData> enchantmentList) {
        ListTag listTag = new ListTag();
        for (int i = 0; i < enchantmentList.size(); i++) {
            CompoundTag enchantmentTag = new CompoundTag();
            enchantmentTag.putString("id", String.valueOf(enchantmentList.get(i).id));
            enchantmentTag.putShort("lvl", (short) enchantmentList.get(i).lvl);
            listTag.add(enchantmentTag);
        }
        return listTag;
    }
}
