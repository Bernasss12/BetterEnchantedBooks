package dev.bernasss12.bebooks.client.gui.entries;

import dev.bernasss12.bebooks.client.gui.ColorDisplayWidget;
import dev.bernasss12.bebooks.util.StringUtils;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.function.Consumer;

public class StringColorEntry extends StringListEntry {

    protected ColorDisplayWidget colorDisplayWidget;

    public StringColorEntry(String fieldName, String value, Consumer<String> saveConsumer) {
        super(fieldName, value, saveConsumer);
        this.colorDisplayWidget = new ColorDisplayWidget(0, 0, 18, StringUtils.getValidIntColor(textFieldWidget.getText()));
    }

    @Override
    public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        this.colorDisplayWidget.y = y + 1;
        if(StringUtils.isValidHexColorString(textFieldWidget.getText())) colorDisplayWidget.setColor(StringUtils.getValidIntColor(textFieldWidget.getText()));
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            this.colorDisplayWidget.x = x + resetButton.getWidth() + textFieldWidget.getWidth();
        } else {
            this.colorDisplayWidget.x = textFieldWidget.x - 21;
        }
        colorDisplayWidget.render(mouseX, mouseY, delta);
    }

    @Override
    protected void textFieldPreRender(TextFieldWidget widget) {
        if (StringUtils.isValidHexColorString(textFieldWidget.getText())) {
            widget.setEditableColor(14737632);
        } else {
            widget.setEditableColor(16733525);
        }
    }
}
