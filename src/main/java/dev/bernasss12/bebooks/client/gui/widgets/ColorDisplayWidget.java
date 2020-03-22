package dev.bernasss12.bebooks.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

import java.awt.*;

public class ColorDisplayWidget extends AbstractButtonWidget {

    protected int color;
    protected int size;

    public ColorDisplayWidget(int x, int y, int size, int color) {
        super(x, y, size, size, "");
        this.color = color;
        this.size = size;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float delta) {
        drawQuad(this.x, this.y, this.x + size, this.y + size, -6250336);
        drawQuad(this.x + 1, this.y + 1, this.x + size - 1, this.y + size - 1, color);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
    }

    private void drawQuad(int x1, int y1, int x2, int y2, int color) {
        Color currentColor = new Color(color);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.disableBlend();
        RenderSystem.disableTexture();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex((double) x1, (double) y2, 0.0D).color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), currentColor.getAlpha()).next();
        bufferBuilder.vertex((double) x2, (double) y2, 0.0D).color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), currentColor.getAlpha()).next();
        bufferBuilder.vertex((double) x2, (double) y1, 0.0D).color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), currentColor.getAlpha()).next();
        bufferBuilder.vertex((double) x1, (double) y1, 0.0D).color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), currentColor.getAlpha()).next();
        tessellator.draw();
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
    }

    public void setColor(int color) {
        this.color = color;
    }
}
