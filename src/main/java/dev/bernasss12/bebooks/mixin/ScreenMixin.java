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
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
    protected TextRenderer textRenderer;

    @Shadow
    protected ItemRenderer itemRenderer;

    @Inject(at = @At(value = "HEAD"),
            method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V")
    private void appendRenderTooltipHead(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo info) {
        BetterEnchantedBooks.enchantedItemStack.set(stack);
    }

    @Inject(at = @At(value = "TAIL"),
            method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V")
    private void appendRenderTooltipTail(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo info) {
        BetterEnchantedBooks.enchantedItemStack.set(ItemStack.EMPTY);
    }

    @Inject(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
            ordinal = 0),
            method = "Lnet/minecraft/client/gui/screen/Screen;renderOrderedTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V"
    )
    private void appendRenderTooltipAfterInvokeImmediateDraw(MatrixStack matrices, List<? extends OrderedText> list, int x, int y, CallbackInfo ci) {
        // This might be redundant but I can't trust everything in minecraft to make sense so this will only run on screens that extend HandledScreen to prevent any kind of random NPEs
        if (MinecraftClient.getInstance().currentScreen != null && MinecraftClient.getInstance().currentScreen instanceof HandledScreen) {
            if (BetterEnchantedBooks.enchantedItemStack.get().isItemEqual(new ItemStack(Items.ENCHANTED_BOOK))) {
                switch (ModConfig.tooltipSetting) {
                    case ENABLED:
                        drawTooltipIcons(list, x, y);
                        break;
                    case ON_SHIFT:
                        if (Screen.hasShiftDown()) drawTooltipIcons(list, x, y);
                        break;
                    case DISABLED:
                        break;
                }
            }
        }
    }

    protected void drawTooltipIcons(List<? extends OrderedText> text, int x, int y) {
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
        RenderSystem.pushMatrix();
        RenderSystem.enableRescaleNormal();
        RenderSystem.translatef(0f, 0f, 401f);
        TooltipDrawerHelper.TooltipQueuedEntry entry = BetterEnchantedBooks.cachedTooltipIcons.get(BetterEnchantedBooks.enchantedItemStack.get());
        translatedY += entry.getFirstLine() * 10 + 12;
        for (Enchantment enchantment : entry.getList()) {
            int xOffset = 4;
            for (ItemStack icon : TooltipDrawerHelper.getAndComputeIfAbsent(enchantment)) {
                if(xOffset > maxLength){
                    translatedY += MinecraftClient.getInstance().textRenderer.fontHeight;
                    xOffset = 4;
                }
                drawScaledItem(itemRenderer, icon, translatedX + xOffset, translatedY, 0.5f);
                xOffset += 8;
            }
            translatedY += 20;
        }
        RenderSystem.translatef(0f, 0f, -401f);
        RenderSystem.popMatrix();
    }

    private void drawScaledItem(ItemRenderer itemRenderer, ItemStack stack, int x, int y, float scale) {
        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);
        RenderSystem.scalef(scale, scale, 1.0f);
        itemRenderer.renderGuiItemIcon(stack, scaledX - 8, scaledY);
        RenderSystem.scalef(1 / scale, 1 / scale, 1.0f);
    }
}
