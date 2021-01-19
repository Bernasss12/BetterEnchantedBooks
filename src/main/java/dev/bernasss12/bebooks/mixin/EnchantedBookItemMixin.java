package dev.bernasss12.bebooks.mixin;

import dev.bernasss12.bebooks.client.gui.BEBooksConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.EnchantedBookItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantedBookItem.class)
@Environment(EnvType.CLIENT)
public abstract class EnchantedBookItemMixin {
    @Inject(method = "hasGlint", at = @At(value = "RETURN"), cancellable = true)
    public void hasGlintReturn(CallbackInfoReturnable<Boolean> info){
        info.setReturnValue(BEBooksConfig.glintSetting);
    }
}
