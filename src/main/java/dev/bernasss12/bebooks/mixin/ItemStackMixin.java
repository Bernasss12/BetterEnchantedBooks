package dev.bernasss12.bebooks.mixin;

import dev.bernasss12.bebooks.client.gui.BEBooksConfig;
import dev.bernasss12.bebooks.util.NBTUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemStack.class)
@Environment(EnvType.CLIENT)
public abstract class ItemStackMixin {

    public ItemStackMixin(Item item) {
        super();
    }

    @Inject(at = @At("HEAD"), method = "appendEnchantments")
    private static void appendEnchantmentsHead(List<Text> tooltip, ListTag enchantments, CallbackInfo info) {
        if (BEBooksConfig.configsFirstLoaded && BEBooksConfig.doSort) {
            ListTag sortedEnchantments;
            try {
                sortedEnchantments = NBTUtils.sort(enchantments, BEBooksConfig.doSortAlphabetically);
                enchantments.clear();
                enchantments.addAll(sortedEnchantments);
            } catch (Exception ignored) {
            }
        }
    }
}
