package dev.bernasss12.bebooks.client.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import static dev.bernasss12.bebooks.BetterEnchantedBooksLegacy.LOGGER;
import static dev.bernasss12.bebooks.util.ModConstants.DEFAULT_BOOK_STRIP_COLOR;
import static dev.bernasss12.bebooks.util.ModConstants.DEFAULT_CHECKED_ITEMS_LIST;
import static dev.bernasss12.bebooks.util.ModConstants.DEFAULT_ENCHANTMENT_COLORS;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ModConfigLegacy {

    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("bebooks");

    public static boolean configsFirstLoaded = false;
    public static Map<String, EnchantmentData> enchantmentDataMap = new HashMap<>();

    // Tooltip Settings
    public static List<ItemStack> checkedItemsList = DEFAULT_CHECKED_ITEMS_LIST; // TODO currently not modifiable

    private static void loadEnchantmentData() {
        Path path = CONFIG_DIR.resolve("enchantment_data.json");
        Gson gson = new Gson();

        if (!Files.exists(path))
            return;

        // Try to parse the enchantment data from the json file
        try {
            String json = Files.readString(path);
            enchantmentDataMap = gson.fromJson(json, new TypeToken<Map<String, EnchantmentData>>() {}.getType());
        } catch (Exception e) {
            LOGGER.error("Couldn't load enchantment data!", e);
        }
    }

    // Note: this should be executed after all mods registered their enchantments (like on the title screen)
    private static void populateEnchantmentData() {
        // Set enchantment values for all existing data entries
        for (var entry : enchantmentDataMap.entrySet()) {
            entry.getValue().enchantment = Registries.ENCHANTMENT.get(Identifier.tryParse(entry.getKey()));
        }

        // Create data entries for all new enchantments
        int index = enchantmentDataMap.size();
        for (var enchantment : Registries.ENCHANTMENT) {
            String id = Objects.requireNonNull(Registries.ENCHANTMENT.getId(enchantment)).toString();
            if (enchantmentDataMap.putIfAbsent(id,
                    new EnchantmentData(enchantment, index, DEFAULT_ENCHANTMENT_COLORS.getOrDefault(enchantment, DEFAULT_BOOK_STRIP_COLOR))) == null)
                index++;
        }
    }

    public static void saveEnchantmentData() {
        Path path = CONFIG_DIR.resolve("enchantment_data.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        // Try to save the enchantment data as a json file
        try {
            String json = gson.toJson(enchantmentDataMap);
            Files.writeString(path, json);
        } catch (Exception e) {
            LOGGER.error("Couldn't save enchantment data!", e);
        }
    }

    public static void loadAndPopulateConfig() {
        loadEnchantmentData();
        populateEnchantmentData();

        configsFirstLoaded = true;
    }

    public static void saveConfig() {
        saveEnchantmentData();
    }

    // Note: transient fields are ignored by Gson. When loaded, the constructor won't be called.
    public static class EnchantmentData {

        public int orderIndex;
        public int color;
        public transient Enchantment enchantment;
        private transient String translatedName;

        public EnchantmentData(Enchantment enchantment, int index, int color) {
            this.orderIndex = index;
            this.color = color;
            this.enchantment = enchantment;
        }

        @NotNull
        public String getTranslatedName() {
            if (enchantment == null) return ""; // dummy value
            if (translatedName != null) return translatedName;

            String translationKey = enchantment.getTranslationKey();

            if (I18n.hasTranslation(translationKey)) {
                this.translatedName = I18n.translate(translationKey);
                return this.translatedName;
            } else {
                return translationKey;
            }
        }
    }
}
