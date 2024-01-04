package dev.bernasss12.bebooks.mixin;

import dev.bernasss12.bebooks.BetterEnchantedBooksLegacy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
@Environment(EnvType.CLIENT)
public abstract class HandledScreenMixin {
    @ModifyArg(
        method = "drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;getTooltipFromItem(Lnet/minecraft/item/ItemStack;)Ljava/util/List;"
        ),
        index = 0
    )
    private ItemStack setEnchantedItemStack(ItemStack stack) {
        BetterEnchantedBooksLegacy.enchantedItemStack.set(stack);
        return stack;
    }

    @Inject(method = "drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V", at = @At(value = "TAIL"))
    private void forgetEnchantedItemStack(DrawContext context, int x, int y, CallbackInfo ci) {
        BetterEnchantedBooksLegacy.enchantedItemStack.set(ItemStack.EMPTY);
    }
}
