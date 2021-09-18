package dev.bernasss12.bebooks.mixin;

import dev.bernasss12.bebooks.BetterEnchantedBooks;
import dev.bernasss12.bebooks.client.gui.ModConfig;
import dev.bernasss12.bebooks.client.gui.TooltipDrawerHelper;
import dev.bernasss12.bebooks.util.NBTUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;

@Mixin(value = ItemStack.class, priority = 10)
@Environment(EnvType.CLIENT)
public abstract class ItemStackMixin {

    @ModifyVariable(method = "appendEnchantments", argsOnly = true, at = @At("HEAD"))
    private static NbtList appendEnchantmentsHead(NbtList tag, List<Text> tooltip, NbtList enchantments) {
        if (MinecraftClient.getInstance().currentScreen instanceof HandledScreen) {
            NbtList sortedEnchantments = NBTUtils.toListTag(NBTUtils.sorted(enchantments, ModConfig.sortingSetting, ModConfig.doKeepCursesBelow));

            if (BetterEnchantedBooks.enchantedItemStack.get().getItem().equals(Items.ENCHANTED_BOOK)) {
                BetterEnchantedBooks.cachedTooltipIcons.putIfAbsent(BetterEnchantedBooks.enchantedItemStack.get(),
                        new TooltipDrawerHelper.TooltipQueuedEntry(tooltip.size(), sortedEnchantments));
            }

            TooltipDrawerHelper.currentTooltipWidth = MinecraftClient.getInstance().textRenderer
                                                              .getWidth(tooltip.stream()
                                                                               .max(Comparator.comparing(line -> MinecraftClient.getInstance().textRenderer.getWidth(line)))
                                                                               .orElse(new LiteralText("")));
            return sortedEnchantments;
        }
        return tag;
    }
    // ItemStack.appendEnchantments's lambda
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(at = @At(value = "HEAD"), method = "method_17869(Ljava/util/List;Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/enchantment/Enchantment;)V")
    private static void setShowEnchantmentMaxLevel(List<Text> tooltip, NbtCompound tag, Enchantment enchantment, CallbackInfo info) {
        if (ModConfig.doShowEnchantmentMaxLevel) {
            BetterEnchantedBooks.shouldShowEnchantmentMaxLevel.set(true);
        }
    }

    // ItemStack.appendEnchantments's lambda
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(at = @At(value = "TAIL"), method = "method_17869(Ljava/util/List;Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/enchantment/Enchantment;)V")
    private static void addTooltipSpacers(List<Text> tooltip, NbtCompound tag, Enchantment enchantment, CallbackInfo info) {
        if (MinecraftClient.getInstance().currentScreen instanceof HandledScreen) {
            if (BetterEnchantedBooks.enchantedItemStack.get().getItem().equals(Items.ENCHANTED_BOOK)) {
                switch (ModConfig.tooltipSetting) {
                    case ENABLED:
                        tooltip.addAll(TooltipDrawerHelper.getSpacerLines(enchantment, TooltipDrawerHelper.currentTooltipWidth));
                        break;
                    case ON_SHIFT:
                        if (Screen.hasShiftDown()) tooltip.addAll(TooltipDrawerHelper.getSpacerLines(enchantment, TooltipDrawerHelper.currentTooltipWidth));
                        break;
                    case DISABLED:
                        break;
                }
            }
        }
    }
}
