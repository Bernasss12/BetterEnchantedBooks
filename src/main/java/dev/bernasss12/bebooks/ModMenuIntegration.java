package dev.bernasss12.bebooks;

import dev.bernasss12.bebooks.client.gui.BEBooksConfig;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public String getModId() {
        return "bebooks";
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> BEBooksConfig.getConfigScreen().build();
    }
}
