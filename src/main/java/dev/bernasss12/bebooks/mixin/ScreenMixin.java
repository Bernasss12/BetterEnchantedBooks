package dev.bernasss12.bebooks.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.bernasss12.bebooks.BetterEnchantedBooks;
import dev.bernasss12.bebooks.client.gui.ModConfig;
import dev.bernasss12.bebooks.client.gui.TooltipDrawerHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
@Environment(EnvType.CLIENT)
public abstract class ScreenMixin extends DrawableHelper {

    @Shadow
    public int width;
    @Shadow
    public int height;

    @Shadow
    protected ItemRenderer itemRenderer;

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

    @Inject(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
            ordinal = 0),
            method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V"
    )
    private void renderEnchantmentTooltipIcons(MatrixStack matrices, List<TooltipComponent> list, int x, int y, CallbackInfo ci) {
        if (BetterEnchantedBooks.enchantedItemStack.get().getItem().equals(Items.ENCHANTED_BOOK)) {
            switch (ModConfig.tooltipSetting) {
                case ENABLED:
                    drawTooltipIcons(matrices, list, x, y);
                    break;
                case ON_SHIFT:
                    if (Screen.hasShiftDown()) drawTooltipIcons(matrices, list, x, y);
                    break;
                case DISABLED:
                    break;
            }
        }
    }

    @Unique
    protected void drawTooltipIcons(MatrixStack matrices, List<TooltipComponent> text, int x, int y) {
        TooltipDrawerHelper.TooltipQueuedEntry entry = BetterEnchantedBooks.cachedTooltipIcons.get(BetterEnchantedBooks.enchantedItemStack.get());
        if (entry == null) return;

        int maxLength = TooltipDrawerHelper.currentTooltipWidth;
        int translatedX = x + 12;
        int translatedY = y - 12;
        int tooltipHeight = 8;
        if (text.size() > 1) {
            tooltipHeight += 2 + (text.size() - 1) * 10;
        }

        if (translatedX + maxLength > this.width) {
            translatedX -= 28 + maxLength;
        }

        if (translatedY + tooltipHeight + 6 > this.height) {
            translatedY = this.height - tooltipHeight - 6;
        }
        matrices.push();
        matrices.translate(0, 0, 401);
        translatedY += entry.getFirstLine() * 10 + 12;
        for (Enchantment enchantment : entry.getList()) {
            int xOffset = 4;
            for (ItemStack icon : TooltipDrawerHelper.getAndComputeIfAbsent(enchantment)) {
                if (xOffset > maxLength){
                    translatedY += MinecraftClient.getInstance().textRenderer.fontHeight;
                    xOffset = 4;
                }
                drawScaledItem(itemRenderer, icon, translatedX + xOffset, translatedY, 0.5f);
                xOffset += 8;
            }
            translatedY += 20;
        }
        matrices.translate(0, 0, -401);
        matrices.pop();
    }

    @Unique
    private static void drawScaledItem(ItemRenderer itemRenderer, ItemStack stack, int x, int y, float scale) {
        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);

        MatrixStack matrices = RenderSystem.getModelViewStack();
        matrices.scale(scale, scale, 1.0f);
        RenderSystem.applyModelViewMatrix();

        itemRenderer.renderGuiItemIcon(stack, scaledX - 8, scaledY);

        matrices.scale(1 / scale, 1 / scale, 1.0f);
        RenderSystem.applyModelViewMatrix();
    }
}
