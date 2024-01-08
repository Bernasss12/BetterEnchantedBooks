package dev.bernasss12.bebooks.gui.tooltip

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.item.ItemStack

data class IconTooltipComponent(val icons: List<ItemStack>) : TooltipComponent {
    override fun getHeight(): Int = MinecraftClient.getInstance().textRenderer.fontHeight + 1

    override fun getWidth(textRenderer: TextRenderer?): Int = icons.size * 8

    override fun drawItems(textRenderer: TextRenderer?, x: Int, y: Int, context: DrawContext) {
        val scale = 0.5f
        val scaledX = (x / scale).toInt()
        val scaledY = (y / scale).toInt()
        val scaledOffset = (8 / scale).toInt()
        val matrices = context.matrices
        matrices.push()
        matrices.scale(0.5f, 0.5f, 1.0f)
        for (i in icons.indices) {
            context.drawItem(icons[i], scaledX + scaledOffset * i, scaledY)
        }
        matrices.pop()
    }
}