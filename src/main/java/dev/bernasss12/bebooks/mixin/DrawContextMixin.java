package dev.bernasss12.bebooks.mixin;

import dev.bernasss12.bebooks.BetterEnchantedBooks;
import dev.bernasss12.bebooks.client.gui.ModConfig;
import dev.bernasss12.bebooks.client.gui.tooltip.IconTooltipComponent;
import dev.bernasss12.bebooks.util.text.IconTooltipDataText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.Items;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(DrawContext.class)
@Environment(EnvType.CLIENT)
public abstract class DrawContextMixin {
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
