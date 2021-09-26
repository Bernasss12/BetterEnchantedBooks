package dev.bernasss12.bebooks.mixin;

import dev.bernasss12.bebooks.BetterEnchantedBooks;
import dev.bernasss12.bebooks.client.gui.ModConfig;
import dev.bernasss12.bebooks.client.gui.tooltip.IconTooltipComponent;
import dev.bernasss12.bebooks.util.text.IconTooltipDataText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(Screen.class)
@Environment(EnvType.CLIENT)
public abstract class ScreenMixin extends DrawableHelper {

    @Inject(at = @At(value = "HEAD"),
            method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V")
    private void setEnchantedItemStack(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo info) {
        BetterEnchantedBooks.enchantedItemStack.set(stack);
    }

    @Inject(at = @At(value = "TAIL"),
            method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V")
    private void forgetEnchantedItemStack(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo info) {
        BetterEnchantedBooks.enchantedItemStack.set(ItemStack.EMPTY);
    }

    @ModifyArg(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V"
            ),
            method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V",
            index = 1
    )
    private List<TooltipComponent> convertTooltipComponents(List<TooltipComponent> components) {
        if (BetterEnchantedBooks.enchantedItemStack.get().getItem().equals(Items.ENCHANTED_BOOK)) {
            if (ModConfig.tooltipSetting == ModConfig.TooltipSetting.ENABLED || (ModConfig.tooltipSetting == ModConfig.TooltipSetting.ON_SHIFT && Screen.hasShiftDown())) {
                return components.stream().map(
                        originalComponent -> {
                            OrderedText text;
                            if (originalComponent instanceof OrderedTextTooltipComponent) {
                                text = ((OrderedTextTooltipComponentAccessor) originalComponent).getText();
                                if (text instanceof IconTooltipDataText) {
                                    return new IconTooltipComponent(((IconTooltipDataText) text).icons());
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
