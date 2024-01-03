package dev.bernasss12.bebooks.client.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.bernasss12.bebooks.BetterEnchantedBooks;
import static dev.bernasss12.bebooks.BetterEnchantedBooks.LOGGER;
import static dev.bernasss12.bebooks.util.ModConstants.*;
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
public class ModConfig {
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("bebooks");

    public static boolean configsFirstLoaded = false;
    public static Map<String, EnchantmentData> enchantmentDataMap = new HashMap<>();

    // Sorting Settings
    public static SortingSetting sortingSetting;
    public static boolean doKeepCursesBelow;

    // Tooltip Settings
    public static List<ItemStack> checkedItemsList = DEFAULT_CHECKED_ITEMS_LIST; // TODO currently not modifiable
    public static boolean doShowEnchantmentMaxLevel;
    public static TooltipSetting tooltipSetting;

    // Coloring Settings
    public static boolean doColorBooks;
    public static boolean doCurseColorOverride;
    public static SortingSetting colorPrioritySetting;

    // Defines whether the enchanted book has a visible glint or not. Default is NOT
    public static Boolean glintSetting;

    public static void loadConfigDefaults() {
        // Sorting Settings
        sortingSetting = DEFAULT_SORTING_SETTING;
        doKeepCursesBelow = DEFAULT_KEEP_CURSES_BELOW;
        // Coloring Settings
        doColorBooks = DEFAULT_COLOR_BOOKS;
        doCurseColorOverride = DEFAULT_CURSE_COLOR_OVERRIDE;
        colorPrioritySetting = DEFAULT_COLOR_PRIORITY_SETTING;
        // Tooltip Settings
        doShowEnchantmentMaxLevel = DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL;
        tooltipSetting = DEFAULT_TOOLTIP_SETTING;
        // Enchantment Glint
        glintSetting = DEFAULT_GLINT_SETTING;
    }

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
            if (enchantmentDataMap.putIfAbsent(id, new EnchantmentData(enchantment, index, DEFAULT_ENCHANTMENT_COLORS.getOrDefault(enchantment, DEFAULT_BOOK_STRIP_COLOR))) == null)
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
        Path path = CONFIG_DIR.resolve("config.properties");

        loadConfigDefaults();

        // Try to read and parse config file
        if (Files.exists(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                Properties properties = new Properties();
                properties.load(reader);

                // Get setting version
                int version = 0;
                if (properties.containsKey("version")) {
                    version = Integer.parseInt(properties.getProperty("version"));
                }

                // Sorting Settings
                if (version == 0) {
                    if (Boolean.parseBoolean(properties.getProperty("sort"))) {
                        if (Boolean.parseBoolean(properties.getProperty("sort_alphabetically"))) {
                            sortingSetting = SortingSetting.ALPHABETICALLY;
                        } else {
                            sortingSetting = SortingSetting.CUSTOM;
                        }
                    } else {
                        sortingSetting = SortingSetting.DISABLED;
                    }
                } else {
                    sortingSetting = SortingSetting.fromString(properties.getProperty("sorting_mode"));
                }
                doKeepCursesBelow = Boolean.parseBoolean(properties.getProperty("keep_curses_below"));

                // Coloring Settings
                doColorBooks = Boolean.parseBoolean(properties.getProperty("color_books"));
                doCurseColorOverride = Boolean.parseBoolean(properties.getProperty("override_curse_color"));
                if (version == 0) {
                    if (Boolean.parseBoolean(properties.getProperty("color_books_based_on_alphabetical_order"))) {
                        colorPrioritySetting = SortingSetting.ALPHABETICALLY;
                    } else {
                        colorPrioritySetting = SortingSetting.CUSTOM;
                    }
                } else {
                    colorPrioritySetting = SortingSetting.fromString(properties.getProperty("color_mode"));
                }

                // Tooltip Settings
                doShowEnchantmentMaxLevel = Boolean.parseBoolean(properties.getProperty("show_max_enchantment_level"));
                tooltipSetting = TooltipSetting.fromString(properties.getProperty("tooltip_mode"));

                // Enchantment Glint
                glintSetting = Boolean.parseBoolean(properties.getProperty("enchanted_book_glint"));
            } catch (Exception e) {
                LOGGER.error("Failed to read config file!", e);
            }
        }

        loadEnchantmentData();
        populateEnchantmentData();

        configsFirstLoaded = true;
    }

    public static void saveConfig() {
        Path path = CONFIG_DIR.resolve("config.properties");

        try {
            Files.createDirectories(CONFIG_DIR);
        } catch (IOException e) {
            LOGGER.error("Couldn't create config directory!", e);
        }

        // Try to write config file
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            Properties properties = new Properties();
            // Settings version
            properties.setProperty("version", SETTINGS_VERSION + "");
            // Sorting Settings
            properties.setProperty("sorting_mode", sortingSetting.toString());
            properties.setProperty("keep_curses_below", doKeepCursesBelow + "");
            // Coloring Settings
            properties.setProperty("color_books", doColorBooks + "");
            properties.setProperty("override_curse_color", doCurseColorOverride + "");
            properties.setProperty("color_mode", colorPrioritySetting.toString());
            // Tooltip Settings
            properties.setProperty("show_max_enchantment_level", doShowEnchantmentMaxLevel + "");
            properties.setProperty("tooltip_mode", tooltipSetting.toString());
            // Enchantment Glint
            properties.setProperty("enchanted_book_glint", glintSetting.toString());
            properties.store(writer, null);
        } catch (Exception e) {
            LOGGER.error("Couldn't save config file!", e);
        }

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
        sortingCategory.addEntry(entryBuilder.startEnumSelector(Text.translatable("entry.bebooks.sorting_settings.sorting_mode"), SortingSetting.class, sortingSetting).setDefaultValue(DEFAULT_SORTING_SETTING).setSaveConsumer(setting -> sortingSetting = setting).build());
        sortingCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.bebooks.sorting_settings.keep_curses_at_bottom"), doKeepCursesBelow).setSaveConsumer((doKeepCursesBelowInput) -> doKeepCursesBelow = doKeepCursesBelowInput).build());

        // Coloring settings page
        bookColoring.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.bebooks.book_glint_settings.active"), glintSetting).setSaveConsumer((showEnchantmentGlint) -> glintSetting = showEnchantmentGlint).build());
        bookColoring.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.bebooks.book_coloring_settings.active"), doColorBooks).setSaveConsumer((doColorBooksInput) -> doColorBooks = doColorBooksInput).build());
        bookColoring.addEntry(entryBuilder.startEnumSelector(Text.translatable("entry.bebooks.book_coloring_settings.color_mode"), SortingSetting.class, colorPrioritySetting).setDefaultValue(DEFAULT_COLOR_PRIORITY_SETTING).setSaveConsumer(setting -> colorPrioritySetting = setting).build());
        bookColoring.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.bebooks.book_coloring_settings.curse_color_override_others"), doCurseColorOverride).setSaveConsumer((doColorOverrideWhenCursedInput) -> doCurseColorOverride = doColorOverrideWhenCursedInput).build());
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
        bookColoring.addEntry(entryBuilder.startSubCategory(Text.translatable("subcategory.bebooks.book_coloring_settings.enchantment_color"), enchantments).build());

        // Tooltip settings page
        tooltipCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.bebooks.tooltip_settings.show_enchantment_max_level"), doShowEnchantmentMaxLevel).setDefaultValue(DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL).setSaveConsumer((showEnchantmentMaxLevel) -> doShowEnchantmentMaxLevel = showEnchantmentMaxLevel).build());
        tooltipCategory.addEntry(entryBuilder.startEnumSelector(Text.translatable("entry.bebooks.tooltip_settings.tooltip_mode"), TooltipSetting.class, tooltipSetting).setDefaultValue(DEFAULT_TOOLTIP_SETTING).setSaveConsumer(setting -> tooltipSetting = setting).build());
        builder.setSavingRunnable(() -> {
            BetterEnchantedBooks.clearCachedColors();
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
            return DEFAULT_TOOLTIP_SETTING;
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
            return DEFAULT_SORTING_SETTING;
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
