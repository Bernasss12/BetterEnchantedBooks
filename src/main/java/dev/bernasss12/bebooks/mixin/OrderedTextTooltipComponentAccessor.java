package dev.bernasss12.bebooks.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OrderedTextTooltipComponent.class)
@Environment(EnvType.CLIENT)
public interface OrderedTextTooltipComponentAccessor {
    @Accessor
    @Final
    OrderedText getText();
}
