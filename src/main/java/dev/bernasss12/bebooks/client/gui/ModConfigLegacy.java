package dev.bernasss12.bebooks.client.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import static dev.bernasss12.bebooks.BetterEnchantedBooksLegacy.LOGGER;
import dev.bernasss12.bebooks.config.ModConfig;
import dev.bernasss12.bebooks.util.BookColorManager;
import static dev.bernasss12.bebooks.util.ModConstants.DEFAULT_BOOK_STRIP_COLOR;
import static dev.bernasss12.bebooks.util.ModConstants.DEFAULT_CHECKED_ITEMS_LIST;
import static dev.bernasss12.bebooks.util.ModConstants.DEFAULT_COLOR_MODE;
import static dev.bernasss12.bebooks.util.ModConstants.DEFAULT_ENCHANTMENT_COLORS;
import static dev.bernasss12.bebooks.util.ModConstants.DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL;
import static dev.bernasss12.bebooks.util.ModConstants.DEFAULT_SORTING_MODE;
import static dev.bernasss12.bebooks.util.ModConstants.DEFAULT_TOOLTIP_MODE;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
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

    @SuppressWarnings("rawtypes")
    public static ConfigBuilder getConfigScreen() {
        // Base config builder
        ConfigBuilder builder = ConfigBuilder.create();
        builder.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/spruce_planks.png"));
        builder.setGlobalized(true);

        // Creating categories
        ConfigCategory sortingCategory = builder.getOrCreateCategory(Text.translatable("category.bebooks.sorting_settings"));
        ConfigCategory bookColoring = builder.getOrCreateCategory(Text.translatable("category.bebooks.book_coloring_settings"));
        ConfigCategory tooltipCategory = builder.getOrCreateCategory(Text.translatable("category.bebooks.tooltip_settings"));

        // Adding entries to the categories
        // Sorting settings page
        builder.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/spruce_planks.png"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        sortingCategory.addEntry(entryBuilder.startEnumSelector(Text.translatable("entry.bebooks.sorting_settings.sorting_mode"), SortingSetting.class,
                ModConfig.INSTANCE.getSortingMode()).setDefaultValue(
                DEFAULT_SORTING_MODE).setSaveConsumer(ModConfig.INSTANCE::setSortingMode).build());
        sortingCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.bebooks.sorting_settings.keep_curses_at_bottom"),
                ModConfig.INSTANCE.getKeepCursesBelow()).setSaveConsumer(
                ModConfig.INSTANCE::setKeepCursesBelow).build());

        // Coloring settings page
        bookColoring.addEntry(
                entryBuilder.startBooleanToggle(Text.translatable("entry.bebooks.book_glint_settings.active"), ModConfig.INSTANCE.getEnchantedBookGlint())
                        .setSaveConsumer(ModConfig.INSTANCE::setEnchantedBookGlint).build());
        bookColoring.addEntry(
                entryBuilder.startBooleanToggle(Text.translatable("entry.bebooks.book_coloring_settings.active"), ModConfig.INSTANCE.getColorBooks())
                        .setSaveConsumer(ModConfig.INSTANCE::setColorBooks).build());
        bookColoring.addEntry(
                entryBuilder.startEnumSelector(Text.translatable("entry.bebooks.book_coloring_settings.color_mode"), SortingSetting.class,
                                ModConfig.INSTANCE.getColorMode())
                        .setDefaultValue(DEFAULT_COLOR_MODE).setSaveConsumer(ModConfig.INSTANCE::setColorMode).build());
        bookColoring.addEntry(
                entryBuilder.startBooleanToggle(Text.translatable("entry.bebooks.book_coloring_settings.curse_color_override_others"),
                                ModConfig.INSTANCE.getOverrideCurseColor())
                        .setSaveConsumer(ModConfig.INSTANCE::setOverrideCurseColor).build());
        ArrayList<AbstractConfigListEntry> enchantments = new ArrayList<>();
        for (var dataEntry : enchantmentDataMap.entrySet()) {
            EnchantmentData enchData = dataEntry.getValue();
            if (enchData.enchantment == null) continue; // not registered

            enchantments.add(entryBuilder
                    .startColorField(Text.literal(enchData.getTranslatedName()), enchData.color)
                    .setDefaultValue(DEFAULT_ENCHANTMENT_COLORS.getOrDefault(enchData.enchantment, DEFAULT_BOOK_STRIP_COLOR))
                    .setSaveConsumer((guiEntryColor) -> {
                        EnchantmentData data = enchantmentDataMap.get(dataEntry.getKey());
                        data.color = guiEntryColor;
                    }).build());
        }
        enchantments.sort(Comparator.comparing(entry -> entry.getFieldName().getString()));
        bookColoring.addEntry(
                entryBuilder.startSubCategory(Text.translatable("subcategory.bebooks.book_coloring_settings.enchantment_color"), enchantments).build());

        // Tooltip settings page
        tooltipCategory.addEntry(
                entryBuilder.startBooleanToggle(Text.translatable("entry.bebooks.tooltip_settings.show_enchantment_max_level"),
                                ModConfig.INSTANCE.getShowMaxEnchantmentLevel())
                        .setDefaultValue(DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL)
                        .setSaveConsumer(ModConfig.INSTANCE::setShowMaxEnchantmentLevel).build());
        tooltipCategory.addEntry(
                entryBuilder.startEnumSelector(Text.translatable("entry.bebooks.tooltip_settings.tooltip_mode"), TooltipSetting.class,
                                ModConfig.INSTANCE.getTooltipMode())
                        .setDefaultValue(DEFAULT_TOOLTIP_MODE).setSaveConsumer(ModConfig.INSTANCE::setTooltipMode).build());
        builder.setSavingRunnable(() -> {
            ModConfig.INSTANCE.save();
            BookColorManager.clear();
            saveConfig();
        });

        return builder;
    }

    public enum TooltipSetting implements SelectionListEntry.Translatable {
        ENABLED,
        ON_SHIFT,
        DISABLED;

        public static TooltipSetting fromString(String string) {
            for (var value : TooltipSetting.values()) {
                if (value.toString().equals(string)) {
                    return value;
                }
            }
            return DEFAULT_TOOLTIP_MODE;
        }

        @Override
        public @NotNull String getKey() {
            return "enum.bebooks.tooltip_settings." + toString().toLowerCase();
        }
    }

    public enum SortingSetting implements SelectionListEntry.Translatable {
        ALPHABETICALLY,
        CUSTOM,
        DISABLED;

        public static SortingSetting fromString(String string) {
            for (var value : SortingSetting.values()) {
                if (value.toString().equals(string)) {
                    return value;
                }
            }
            return DEFAULT_SORTING_MODE;
        }

        @Override
        public @NotNull String getKey() {
            return "enum.bebooks.sorting_settings." + toString().toLowerCase();
        }
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
