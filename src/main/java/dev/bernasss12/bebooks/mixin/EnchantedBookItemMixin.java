package dev.bernasss12.bebooks.mixin;

import net.minecraft.item.EnchantedBookItem;

import dev.bernasss12.bebooks.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantedBookItem.class)
@Environment(EnvType.CLIENT)
public abstract class EnchantedBookItemMixin {
    @Inject(method = "hasGlint", at = @At(value = "RETURN"), cancellable = true)
    public void hasGlintReturn(CallbackInfoReturnable<Boolean> info){
        info.setReturnValue(ModConfig.INSTANCE.getEnchantedBookGlint());
    }
}
