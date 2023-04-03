package dev.bernasss12.bebooks.client.gui.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import java.util.List;

public record IconTooltipComponent(List<ItemStack> icons) implements TooltipComponent {

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return icons.size() * 8;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack _matrices, ItemRenderer itemRenderer) {
        float scale = 0.5f;
        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);
        int scaledOffset =  (int) (8 / scale);
        MatrixStack matrices = RenderSystem.getModelViewStack();
        matrices.push();
        matrices.scale(0.5f, 0.5f, 1.0f);
        for(int i = 0; i < icons.size(); i++){
            itemRenderer.renderInGuiWithOverrides(matrices, icons.get(i), scaledX + scaledOffset * i, scaledY, -1);
        }
        matrices.pop();
        RenderSystem.applyModelViewMatrix();
    }
}
