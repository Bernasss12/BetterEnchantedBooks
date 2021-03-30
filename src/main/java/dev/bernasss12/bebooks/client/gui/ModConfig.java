package dev.bernasss12.bebooks.client.gui;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import dev.bernasss12.bebooks.BetterEnchantedBooks;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static dev.bernasss12.bebooks.BetterEnchantedBooks.LOGGER;
import static dev.bernasss12.bebooks.client.gui.ModConstants.*;

@Environment(EnvType.CLIENT)
public class ModConfig {


    public static boolean configsFirstLoaded = false;
    public static Map<String, EnchantmentData> enchantmentDataMap;
    public static Map<String, Integer> mappedEnchantmentColors;
    public static Map<String, Integer> mappedEnchantmentIndices;
    public static Map<String, EnchantmentTarget> mappedEnchantmentTargets;

    // Tooltip information settings
    public static boolean doShowEnchantmentMaxLevel = DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL;

    // Tooltip Icon Settings
    public static List<ItemStack> checkedItemsList = DEFAULT_CHECKED_ITEMS_LIST;

    // Default enchantment colors

    public static TooltipSetting tooltipSetting;

    // Sorting Settings
    public static SortingSetting sortingSetting;
    public static boolean doKeepCursesBelow;

    // Coloring Settings
    public static boolean doColorBooks;
    public static boolean doCurseColorOverride;
    public static SortingSetting colorPrioritySetting;

    // Default minecraft book color, sorta

    // Defines whether the enchanted book has a visible glint or not. Default is NOT
    public static Boolean glintSetting;

    public static void loadEnchantmentData() {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "bebooks/enchantment_data.json");
        Gson gson = new Gson();
        mappedEnchantmentTargets = new HashMap<>();
        // Try and read the file and parse the json.
        try {
            if (file.getParentFile().mkdirs()) LOGGER.info("Config folder created!");
            Map<String, StoredEnchantmentData> storedEnchantmentDataMap;
            storedEnchantmentDataMap = gson.fromJson(new InputStreamReader(new FileInputStream(file)), new TypeToken<Map<String, StoredEnchantmentData>>() {
            }.getType());
            enchantmentDataMap = fromStoredData(storedEnchantmentDataMap);
        } catch (Exception e) {
            LOGGER.error("Couldn't load enchantment data", e);
            // In case map parsing fails create a new empty map and populate it with all registered enchantments with the default color.
            enchantmentDataMap = new HashMap<>();
        }
        int index = enchantmentDataMap.size();
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            String id = Objects.requireNonNull(Registry.ENCHANTMENT.getId(enchantment)).toString();
            mappedEnchantmentTargets.putIfAbsent(id, enchantment.type);
            if (enchantmentDataMap.putIfAbsent(id, new EnchantmentData(enchantment, I18n.translate(enchantment.getTranslationKey()), index, DEFAULT_ENCHANTMENT_COLORS.getOrDefault(enchantment, DEFAULT_BOOK_STRIP_COLOR))) == null)
                index++;
        }
        mappedEnchantmentColors = new HashMap<>();
        mappedEnchantmentIndices = new HashMap<>();
        enchantmentDataMap.forEach((key, enchantmentData) -> {
            mappedEnchantmentColors.putIfAbsent(key, enchantmentData.color);
            mappedEnchantmentIndices.putIfAbsent(key, enchantmentData.orderIndex);
        });
        saveEnchantmentData();
    }

    public static void saveEnchantmentData() {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "bebooks/enchantment_data.json");
        Gson gson = new Gson();
        // Try and write the Map to the json config file.
        try {
            FileWriter writer = new FileWriter(file);
            gson.toJson(toStoredData(enchantmentDataMap), writer);
            writer.close();
        } catch (JsonIOException | IOException e) {
            LOGGER.error("Couldn't save enchantment data", e);
        }
    }

    public static void loadConfig() {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "bebooks/config.properties");
        int version;
        try {
            if (file.getParentFile().mkdirs()) LOGGER.info("Config folder created!");
            // Sorting Settings
            sortingSetting = DEFAULT_SORTING_SETTING;
            doKeepCursesBelow = DEFAULT_KEEP_CURSES_BELOW;
            // Coloring Settings
            doColorBooks = DEFAULT_COLOR_BOOKS;
            doCurseColorOverride = DEFAULT_CURSE_COLOR_OVERRIDE; // TODO implement
            colorPrioritySetting = DEFAULT_COLOR_PRIORITY_SETTING;
            // Tooltip Settings
            doShowEnchantmentMaxLevel = DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL;
            tooltipSetting = DEFAULT_TOOLTIP_SETTING;
            // Enchantment Glint
            glintSetting = DEFAULT_GLINT_SETTING;
            loadEnchantmentData();
            if (!file.exists()) {
                saveConfig();
            }
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            // Get setting version
            if (properties.containsKey("version")) {
                version = Integer.parseInt(properties.getProperty("version"));
            } else {
                version = 0;
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
            saveConfig();
        } catch (Exception e) {
            e.printStackTrace();
            // Sorting Settings
            sortingSetting = DEFAULT_SORTING_SETTING;
            doKeepCursesBelow = DEFAULT_KEEP_CURSES_BELOW;
            // Coloring Settings
            doColorBooks = DEFAULT_COLOR_BOOKS;
            doCurseColorOverride = DEFAULT_CURSE_COLOR_OVERRIDE; // TODO implement
            colorPrioritySetting = DEFAULT_COLOR_PRIORITY_SETTING;
            // Tooltip settings
            doShowEnchantmentMaxLevel = DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL;
            tooltipSetting = DEFAULT_TOOLTIP_SETTING;
            loadEnchantmentData();
            try {
                Files.deleteIfExists(file.toPath());
            } catch (Exception ignored) {
            }
        }
        saveConfig();
        configsFirstLoaded = true;
    }

    public static void saveConfig() {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "bebooks/config.properties");
        BetterEnchantedBooks.clearCachedColors();
        try {
            FileWriter writer = new FileWriter(file, false);
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
            saveEnchantmentData();
            properties.store(writer, null);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Sorting Settings
            sortingSetting = DEFAULT_SORTING_SETTING;
            doKeepCursesBelow = DEFAULT_KEEP_CURSES_BELOW;
            // Coloring Settings
            doColorBooks = DEFAULT_COLOR_BOOKS;
            doCurseColorOverride = DEFAULT_CURSE_COLOR_OVERRIDE;
            colorPrioritySetting = DEFAULT_COLOR_PRIORITY_SETTING;
            // Tooltip Setting
            doShowEnchantmentMaxLevel = DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL;
            tooltipSetting = DEFAULT_TOOLTIP_SETTING;
            // Enchantment Glint
            glintSetting = DEFAULT_GLINT_SETTING;
            saveEnchantmentData();
        }
    }

    public static ConfigBuilder getConfigScreen() {
        // Base config builder
        ConfigBuilder builder = ConfigBuilder.create();
        builder.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/spruce_planks.png"));
        builder.setGlobalized(true);
        // Creating categories
        ConfigCategory sortingCategory = builder.getOrCreateCategory(new TranslatableText("category.bebooks.sorting_settings"));
        ConfigCategory bookColoring = builder.getOrCreateCategory(new TranslatableText("category.bebooks.book_coloring_settings"));
        ConfigCategory tooltipCategory = builder.getOrCreateCategory(new TranslatableText("category.bebooks.tooltip_settings"));
        // Adding entries to the categories
        // Sorting settings page
        builder.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/spruce_planks.png"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        sortingCategory.addEntry(entryBuilder.startEnumSelector(new TranslatableText("entry.bebooks.sorting_settings.sorting_mode"), SortingSetting.class, sortingSetting).setDefaultValue(DEFAULT_SORTING_SETTING).setSaveConsumer(setting -> sortingSetting = setting).build());
        sortingCategory.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("entry.bebooks.sorting_settings.keep_curses_at_bottom"), doKeepCursesBelow).setSaveConsumer((doKeepCursesBelowInput) -> doKeepCursesBelow = doKeepCursesBelowInput).build());
        // Coloring settings page
        bookColoring.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("entry.bebooks.book_glint_settings.active"), glintSetting).setSaveConsumer((showEnchantmentGlint) -> glintSetting = showEnchantmentGlint).build());
        bookColoring.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("entry.bebooks.book_coloring_settings.active"), doColorBooks).setSaveConsumer((doColorBooksInput) -> doColorBooks = doColorBooksInput).build());
        bookColoring.addEntry(entryBuilder.startEnumSelector(new TranslatableText("entry.bebooks.book_coloring_settings.color_mode"), SortingSetting.class, colorPrioritySetting).setDefaultValue(DEFAULT_COLOR_PRIORITY_SETTING).setSaveConsumer(setting -> colorPrioritySetting = setting).build());
        bookColoring.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("entry.bebooks.book_coloring_settings.curse_color_override_others"), doCurseColorOverride).setSaveConsumer((doColorOverrideWhenCursedInput) -> doCurseColorOverride = doColorOverrideWhenCursedInput).build());
        ArrayList<AbstractConfigListEntry> enchantments = new ArrayList<>();
        for (Map.Entry<String, EnchantmentData> enchantmentDataEntry : enchantmentDataMap.entrySet()) {
            enchantments.add(entryBuilder.startColorField(new LiteralText(enchantmentDataEntry.getValue().translatedName), mappedEnchantmentColors.get(enchantmentDataEntry.getKey())).setDefaultValue(DEFAULT_ENCHANTMENT_COLORS.getOrDefault(enchantmentDataEntry.getValue().enchantment, DEFAULT_BOOK_STRIP_COLOR)).setSaveConsumer((guiEntryColor) ->
            {
                EnchantmentData data = enchantmentDataMap.get(enchantmentDataEntry.getKey());
                data.color = guiEntryColor;
                enchantmentDataMap.replace(enchantmentDataEntry.getKey(), data);
            }).build());
        }
        enchantments.sort(Comparator.comparing(entry -> entry.getFieldName().asString()));
        bookColoring.addEntry(entryBuilder.startSubCategory(new TranslatableText("subcategory.bebooks.book_coloring_settings.enchantment_color"), enchantments).build());
        // Tooltip settings page
        tooltipCategory.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("entry.bebooks.tooltip_settings.show_enchantment_max_level"), doShowEnchantmentMaxLevel).setDefaultValue(DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL).setSaveConsumer((showEnchantmentMaxLevel) -> doShowEnchantmentMaxLevel = showEnchantmentMaxLevel).build());
        tooltipCategory.addEntry(entryBuilder.startEnumSelector(new TranslatableText("entry.bebooks.tooltip_settings.tooltip_mode"), TooltipSetting.class, tooltipSetting).setDefaultValue(DEFAULT_TOOLTIP_SETTING).setSaveConsumer(setting -> tooltipSetting = setting).build());
        builder.setSavingRunnable(() -> {
            saveConfig();
            loadConfig();
        });
        return builder;
    }

    public enum TooltipSetting implements SelectionListEntry.Translatable {
        ENABLED,
        ON_SHIFT,
        DISABLED;

        public static TooltipSetting fromString(String string) {
            for (TooltipSetting value : TooltipSetting.values()) {
                if (value.toString().equals(string)) {
                    return value;
                }
            }
            return DEFAULT_TOOLTIP_SETTING;
        }

        @Override
        public String getKey() {
            return "enum.bebooks.tooltip_settings." + toString().toLowerCase();
        }
    }

    public enum SortingSetting implements SelectionListEntry.Translatable {
        ALPHABETICALLY,
        CUSTOM,
        DISABLED;

        public static SortingSetting fromString(String string) {
            for (SortingSetting value : SortingSetting.values()) {
                if (value.toString().equals(string)) {
                    return value;
                }
            }
            return DEFAULT_SORTING_SETTING;
        }

        @Override
        public String getKey() {
            return "enum.bebooks.sorting_settings." + toString().toLowerCase();
        }
    }

    public static class StoredEnchantmentData {
        public int orderIndex;
        public int color;

        public StoredEnchantmentData(int index, int color) {
            this.orderIndex = index;
            this.color = color;
        }

        public StoredEnchantmentData(EnchantmentData data) {
            this.orderIndex = data.orderIndex;
            this.color = data.color;
        }
    }

    public static class EnchantmentData extends StoredEnchantmentData {
        @NotNull public String translatedName;
        public Enchantment enchantment;

        public EnchantmentData(Enchantment enchantment, @NotNull String translatedName, int index, int color) {
            super(index, color);
            this.enchantment = enchantment;
            this.translatedName = translatedName;
        }

        public EnchantmentData(StoredEnchantmentData data, String key) {
            super(data.orderIndex, data.color);
            Optional<Enchantment> enchantment = Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(key));
            enchantment.ifPresent(value -> this.enchantment = value);
            this.translatedName = enchantment.isPresent() ? enchantment.get().getName(1).getString() : "Error translating.";
        }
    }

    private static HashMap<String, EnchantmentData> fromStoredData(Map<String, StoredEnchantmentData> storedData) {
        HashMap<String, EnchantmentData> result = new HashMap<>();
        for (Map.Entry<String, StoredEnchantmentData> entry : storedData.entrySet()) {
            result.putIfAbsent(entry.getKey(), new EnchantmentData(entry.getValue(), entry.getKey()));
        }
        return result;
    }

    private static HashMap<String, StoredEnchantmentData> toStoredData(Map<String, EnchantmentData> data) {
        HashMap<String, StoredEnchantmentData> result = new HashMap<>();
        for (Map.Entry<String, EnchantmentData> entry : data.entrySet()) {
            result.putIfAbsent(entry.getKey(), new StoredEnchantmentData(entry.getValue()));
        }
        return result;
    }
}
