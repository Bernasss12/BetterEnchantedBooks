package dev.bernasss12.bebooks.mixin;

import dev.bernasss12.bebooks.BetterEnchantedBooks;
import dev.bernasss12.bebooks.client.gui.ModConfig;
import dev.bernasss12.bebooks.client.gui.tooltip.IconTooltipComponent;
import dev.bernasss12.bebooks.util.text.IconTooltipDataText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(DrawContext.class)
@Environment(EnvType.CLIENT)
public abstract class DrawContextMixin {
    // called for Merchant items, but not for inventory items
    @Inject(
        method = "drawItemTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V",
        at = @At("HEAD")
    )
    private void setEnchantedItemStack(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        BetterEnchantedBooks.enchantedItemStack.set(stack);
    }

    @Inject(method = "drawItemTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "TAIL"))
    private void forgetEnchantedItemStack(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        BetterEnchantedBooks.enchantedItemStack.set(ItemStack.EMPTY);
    }

    @ModifyVariable(
        at = @At("HEAD"),
        method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
        argsOnly = true
    )
    private List<TooltipComponent> convertTooltipComponents(List<TooltipComponent> components) {
        if (BetterEnchantedBooks.enchantedItemStack.get().getItem().equals(Items.ENCHANTED_BOOK)) {
            if (ModConfig.tooltipSetting == ModConfig.TooltipSetting.ENABLED || (ModConfig.tooltipSetting == ModConfig.TooltipSetting.ON_SHIFT && Screen.hasShiftDown())) {
                return components.stream().map(
                    originalComponent -> {
                        if (originalComponent instanceof OrderedTextTooltipComponent) {
                            OrderedText text = ((OrderedTextTooltipComponentAccessor) originalComponent).getText();
                            if (text instanceof IconTooltipDataText dataText) {
                                return new IconTooltipComponent(dataText.icons());
                            }
                        }
                        return originalComponent;
                    }
                ).collect(Collectors.toList());
            }
        }
        return components;
    }
}
