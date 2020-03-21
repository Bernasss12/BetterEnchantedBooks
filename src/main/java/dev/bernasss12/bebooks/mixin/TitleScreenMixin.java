package dev.bernasss12.bebooks.mixin;

import dev.bernasss12.bebooks.client.gui.config.BEBooksConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
@Environment(EnvType.CLIENT)
public abstract class TitleScreenMixin {
    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        if (!BEBooksConfig.configsFirstLoaded) BEBooksConfig.loadConfig();
    }
}