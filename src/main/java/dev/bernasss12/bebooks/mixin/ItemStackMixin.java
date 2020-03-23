package dev.bernasss12.bebooks.mixin;

import dev.bernasss12.bebooks.client.gui.BEBooksConfig;
import dev.bernasss12.bebooks.util.NBTUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemStack.class)
@Environment(EnvType.CLIENT)
public abstract class ItemStackMixin {

    @Shadow public abstract boolean canDestroy(RegistryTagManager manager, CachedBlockPosition pos);

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

    @Inject(at = @At(value = "TAIL"), method = "net/minecraft/item/ItemStack.method_17869(Ljava/util/List;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/enchantment/Enchantment;)V")
    private static void appendEnchantmentsAfterInvoke(List<Text> tooltip, CompoundTag tag, Enchantment enchantment, CallbackInfo info) {
        switch (BEBooksConfig.tooltipSetting){
            case ENABLED:
                tooltip.add(new LiteralText(""));
                break;
            case ON_SHIFT:
                if(Screen.hasShiftDown()) tooltip.add(new LiteralText(""));
                break;
            case DISABLED:
                break;
        }
    }
}
