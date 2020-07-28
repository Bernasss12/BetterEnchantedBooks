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
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

@Environment(EnvType.CLIENT)
public class BEBooksConfig {

    private static final int SETTINGS_VERSION = 2;

    public static boolean configsFirstLoaded = false;
    public static Map<String, EnchantmentData> storedEnchantmentData;
    public static Map<String, Integer> mappedEnchantmentColors;
    public static Map<String, Integer> mappedEnchantmentIndices;
    public static Map<String, EnchantmentTarget> mappedEnchantmentTargets;

    // Tooltip Icon Settings
    public static final List<ItemStack> DEFAULT_CHECKED_ITEMS_LIST = Arrays.asList(
            new ItemStack(Items.DIAMOND_SWORD),
            new ItemStack(Items.DIAMOND_PICKAXE),
            new ItemStack(Items.DIAMOND_AXE),
            new ItemStack(Items.DIAMOND_SHOVEL),
            new ItemStack(Items.DIAMOND_HOE),
            new ItemStack(Items.BOW),
            new ItemStack(Items.CROSSBOW),
            new ItemStack(Items.FISHING_ROD),
            new ItemStack(Items.TRIDENT),
            new ItemStack(Items.DIAMOND_HELMET),
            new ItemStack(Items.DIAMOND_CHESTPLATE),
            new ItemStack(Items.DIAMOND_LEGGINGS),
            new ItemStack(Items.DIAMOND_BOOTS),
            new ItemStack(Items.ELYTRA)
    );
    public static List<ItemStack> checkedItemsList = DEFAULT_CHECKED_ITEMS_LIST;

    private static final TooltipSetting DEFAULT_TOOLTIP_SETTING = TooltipSetting.ON_SHIFT;
    public static TooltipSetting tooltipSetting;

    // Sorting Settings
    private static final SortingSetting DEFAULT_SORTING_SETTING = SortingSetting.ALPHABETICALLY;
    public static SortingSetting sortingSetting;
    private static final boolean DEFAULT_KEEP_CURSES_BELOW = true;
    public static boolean doKeepCursesBelow;
    // Coloring Settings
    private static final boolean DEFAULT_COLOR_BOOKS = true;
    public static boolean doColorBooks;
    private static final boolean DEFAULT_CURSE_COLOR_OVERRIDE = true;
    public static boolean doCurseColorOverride;
    private static final SortingSetting DEFAULT_COLOR_PRIORITY_SETTING = SortingSetting.ALPHABETICALLY;
    public static SortingSetting colorPrioritySetting;

    // Default minecraft book color, sorta
    public static final int DEFAULT_BOOK_STRIP_COLOR = 0xc5133a;

    public static void loadEnchantmentData() {
        File file = new File(FabricLoader.getInstance().getConfigDirectory(), "bebooks/enchantment_data.json");
        Gson gson = new Gson();
        mappedEnchantmentTargets = new HashMap<>();
        // Try and read the file and parse the json.
        try {
            if (file.getParentFile().mkdirs()) System.out.println("[BEBooks] Config folder created!");
            storedEnchantmentData = gson.fromJson(new InputStreamReader(new FileInputStream(file)), new TypeToken<Map<String, EnchantmentData>>() {
            }.getType());
        } catch (Exception e) {
            // In case map parsing fails create a new empty map and populate it with all registered enchantments with the default color.
            System.err.println(e);
            storedEnchantmentData = new HashMap<>();
        }
        int index = storedEnchantmentData.size();
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            String id = Objects.requireNonNull(Registry.ENCHANTMENT.getId(enchantment)).toString();
            mappedEnchantmentTargets.putIfAbsent(id, enchantment.type);
            if (storedEnchantmentData.putIfAbsent(id, new EnchantmentData(I18n.translate(enchantment.getTranslationKey()), index, DEFAULT_BOOK_STRIP_COLOR)) == null)
                index++;
        }
        mappedEnchantmentColors = new HashMap<>();
        mappedEnchantmentIndices = new HashMap<>();
        storedEnchantmentData.forEach((key, enchantmentData) -> {
            mappedEnchantmentColors.putIfAbsent(key, enchantmentData.color);
            mappedEnchantmentIndices.putIfAbsent(key, enchantmentData.orderIndex);
        });
        saveEnchantmentData();
    }

    public static void saveEnchantmentData() {
        File file = new File(FabricLoader.getInstance().getConfigDirectory(), "bebooks/enchantment_data.json");
        Gson gson = new Gson();
        // Try and write the Map to the json config file.
        try {
            FileWriter writer = new FileWriter(file);
            gson.toJson(storedEnchantmentData, writer);
            writer.close();
        } catch (JsonIOException | IOException e) {
            // Upon failure print the exception.
            System.err.println(e);
        }
    }

    public static void loadConfig() {
        File file = new File(FabricLoader.getInstance().getConfigDirectory(), "bebooks/config.properties");
        int version;
        try {
            if (file.getParentFile().mkdirs()) System.out.println("[BEBooks] Config folder created!");
            // ThreadLocal
            // Sorting Settings
            sortingSetting = DEFAULT_SORTING_SETTING;
            doKeepCursesBelow = DEFAULT_KEEP_CURSES_BELOW;
            // Coloring Settings
            doColorBooks = DEFAULT_COLOR_BOOKS;
            doCurseColorOverride = DEFAULT_CURSE_COLOR_OVERRIDE; // TODO implement
            colorPrioritySetting = DEFAULT_COLOR_PRIORITY_SETTING;
            // Tooltip Settings
            tooltipSetting = DEFAULT_TOOLTIP_SETTING;
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
            tooltipSetting = TooltipSetting.fromString(properties.getProperty("tooltip_mode"));
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
        File file = new File(FabricLoader.getInstance().getConfigDirectory(), "bebooks/config.properties");
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
            properties.setProperty("tooltip_mode", tooltipSetting.toString());
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
            doCurseColorOverride = DEFAULT_CURSE_COLOR_OVERRIDE; // TODO implement
            colorPrioritySetting = DEFAULT_COLOR_PRIORITY_SETTING;
            // Tooltip Setting
            tooltipSetting = DEFAULT_TOOLTIP_SETTING;
            saveEnchantmentData();
        }
    }

    public static ConfigBuilder getConfigScreen() {
        // Base config builder
        ConfigBuilder builder = ConfigBuilder.create();
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
        bookColoring.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("entry.bebooks.book_coloring_settings.active"), doColorBooks).setSaveConsumer((doColorBooksInput) -> doColorBooks = doColorBooksInput).build());
        bookColoring.addEntry(entryBuilder.startEnumSelector(new TranslatableText("entry.bebooks.book_coloring_settings.color_mode"), SortingSetting.class, colorPrioritySetting).setDefaultValue(DEFAULT_COLOR_PRIORITY_SETTING).setSaveConsumer(setting -> colorPrioritySetting = setting).build());
        bookColoring.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("entry.bebooks.book_coloring_settings.curse_color_override_others"), doCurseColorOverride).setSaveConsumer((doColorOverrideWhenCursedInput) -> doCurseColorOverride = doColorOverrideWhenCursedInput).build());
        ArrayList<AbstractConfigListEntry> enchantments = new ArrayList<>();
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            String key = Registry.ENCHANTMENT.getId(enchantment).toString();
            enchantments.add(entryBuilder.startColorField(new TranslatableText(enchantment.getTranslationKey()), mappedEnchantmentColors.get(key)).setSaveConsumer((string) ->
            {
                EnchantmentData data = storedEnchantmentData.get(key);
                data.color = string;
                data.translatedName = "name";
                storedEnchantmentData.replace(key, data);
            }).build());
        }
        enchantments.sort(Comparator.comparing(entry -> I18n.translate(entry.getFieldName().asString())));
        bookColoring.addEntry(entryBuilder.startSubCategory(new TranslatableText("subcategory.bebooks.book_coloring_settings.enchantment_color"), enchantments).build());
        // Tooltip settings page
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

    public static class EnchantmentData {
        public String translatedName;
        public int orderIndex;
        public int color;

        public EnchantmentData(String translatedName, int index, int color) {
            this.orderIndex = index;
            this.color = color;
            this.translatedName = translatedName;
        }

        @Override
        public String toString() {
            return "index:" + orderIndex + "name:\"" + translatedName + "\",color:" + color;
        }
    }
}
