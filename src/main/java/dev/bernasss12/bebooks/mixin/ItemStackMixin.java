package dev.bernasss12.bebooks.mixin;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;

import dev.bernasss12.bebooks.BetterEnchantedBooksLegacy;
import dev.bernasss12.bebooks.config.ModConfig;
import dev.bernasss12.bebooks.util.NBTUtils;
import dev.bernasss12.bebooks.util.text.IconTooltipDataText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemStack.class, priority = 10)
@Environment(EnvType.CLIENT)
public abstract class ItemStackMixin {

    @ModifyVariable(method = "appendEnchantments", argsOnly = true, at = @At("HEAD"))
    private static NbtList appendEnchantmentsHead(NbtList tag, List<Text> tooltip, NbtList enchantments) {
        if (MinecraftClient.getInstance().currentScreen instanceof HandledScreen) {
            return NBTUtils.toListTag(NBTUtils.sorted(enchantments, ModConfig.INSTANCE.getSortingMode(), ModConfig.INSTANCE.getKeepCursesBelow()));
        }
        return tag;
    }

    @Dynamic("ItemStack.appendEnchantments's lambda")
    @Inject(at = @At(value = "HEAD"), method = "method_17869", remap = false)
    private static void setShowEnchantmentMaxLevel(List<Text> tooltip, NbtCompound tag, Enchantment enchantment, CallbackInfo info) {
        if (ModConfig.INSTANCE.getShowMaxEnchantmentLevel()) {
            BetterEnchantedBooksLegacy.shouldShowEnchantmentMaxLevel.set(true);
        }
    }

    @Dynamic("ItemStack.appendEnchantments's lambda")
    @Inject(at = @At(value = "TAIL"), method = "method_17869", remap = false)
    private static void addTooltipIcons(List<Text> tooltip, NbtCompound tag, Enchantment enchantment, CallbackInfo info) {
        if (MinecraftClient.getInstance().currentScreen instanceof HandledScreen) {
            if (BetterEnchantedBooksLegacy.enchantedItemStack.get().getItem().equals(Items.ENCHANTED_BOOK)) {
                switch (ModConfig.INSTANCE.getTooltipMode()) {
                    case ENABLED:
                        tooltip.add(new IconTooltipDataText(BetterEnchantedBooksLegacy.getApplicableItems(enchantment)));
                        break;
                    case ON_SHIFT:
                        if (Screen.hasShiftDown())
                            tooltip.add(new IconTooltipDataText(BetterEnchantedBooksLegacy.getApplicableItems(enchantment)));
                        break;
                    case DISABLED:
                        break;
                }
            }
        }
    }
}
