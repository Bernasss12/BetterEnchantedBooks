package dev.bernasss12.bebooks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnchantmentData {
    public String translatedName;
    public int orderIndex;
    public int color;

    public EnchantmentData(String translatedName, int index, int color) {
        this.orderIndex = index;
        this.color = color;
        this.translatedName = translatedName;
    }
}
