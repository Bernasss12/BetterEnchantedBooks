package dev.bernasss12.bebooks.client.gui;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import dev.bernasss12.bebooks.BetterEnchantedBooks;
import dev.bernasss12.bebooks.client.gui.entries.StringColorEntry;
import dev.bernasss12.bebooks.util.StringUtils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

@Environment(EnvType.CLIENT)
public class BEBooksConfig {

    public static boolean configsFirstLoaded = false;

    public static Map<String, EnchantmentData> storedEnchantmentData;
    public static Map<String, Integer> mappedEnchantmentColors;
    public static Map<String, Integer> mappedEnchantmentIndices;
    // TODO Possibly will be an Enum with 3 different options in the future. (doSort and doSortAlphabetically -> EnumSortingMode(NONE, ALPHABETICAL, PRIORITY)
    // Sorting Settings
    public static boolean doSort = true;
    public static boolean doSortAlphabetically = true;
    public static boolean doKeepCursesBelow = true;
    // Coloring Settings
    public static boolean doColorBooks = true;
    public static boolean doColorOverrideWhenCursed = true;
    public static boolean doColorBasedOnAlphabeticalOrder = true;

    // Default minecraft book color, sorta
    public static int defaultBookStripColor = 0xc5133a;

    public static void loadEnchantmentData() {
        File file = new File(FabricLoader.getInstance().getConfigDirectory(), "bebooks/enchantment_data.json");
        Gson gson = new Gson();
        // Try and read the file and parse the json.
        try {
            if (file.getParentFile().mkdirs()) System.out.println("[BEBooks] Config folder created!");
            storedEnchantmentData = gson.fromJson(new InputStreamReader(new FileInputStream(file)), new TypeToken<Map<String, EnchantmentData>>() {
            }.getType());
            // After parsing the stored map information try and add any absent enchantments that may have been added since the last configuration of the mod.
            int index = storedEnchantmentData.size();
            for (Enchantment enchantment : Registry.ENCHANTMENT) {
                if (storedEnchantmentData.putIfAbsent(Objects.requireNonNull(Registry.ENCHANTMENT.getId(enchantment)).toString(), new EnchantmentData(I18n.translate(enchantment.getTranslationKey()), index, defaultBookStripColor)) == null)
                    index++;
            }
        } catch (Exception e) {
            // In case map parsing fails create a new empty map and populate it with all registered enchantments with the default color.
            System.err.println(e);
            storedEnchantmentData = new HashMap<>();
            int index = storedEnchantmentData.size();
            for (Enchantment enchantment : Registry.ENCHANTMENT) {
                //String translationKey = I18n.translate(enchantment.getTranslationKey());
                if (storedEnchantmentData.putIfAbsent(Objects.requireNonNull(Registry.ENCHANTMENT.getId(enchantment)).toString(), new EnchantmentData(I18n.translate(enchantment.getTranslationKey()), index, defaultBookStripColor)) == null)
                    index++;
            }
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
        try {
            if (file.getParentFile().mkdirs()) System.out.println("[BEBooks] Config folder created!");
            // Sorting Settings
            doSort = true;
            doSortAlphabetically = true;
            doKeepCursesBelow = true;
            // Coloring Settings
            doColorBooks = true;
            doColorOverrideWhenCursed = true; // TODO implement
            doColorBasedOnAlphabeticalOrder = true;
            loadEnchantmentData();
            if (!file.exists()) {
                saveConfig();
            }
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            // Sorting Settings
            doSort = Boolean.parseBoolean(properties.getProperty("sort"));
            doSortAlphabetically = Boolean.parseBoolean(properties.getProperty("sort_alphabetically"));
            doKeepCursesBelow = Boolean.parseBoolean(properties.getProperty("keep_curses_below"));
            // Coloring Settings
            doColorBooks = Boolean.parseBoolean(properties.getProperty("color_books"));
            doColorOverrideWhenCursed = Boolean.parseBoolean(properties.getProperty("override_curse_color"));
            doColorBasedOnAlphabeticalOrder = Boolean.parseBoolean(properties.getProperty("color_books_based_on_alphabetical_order"));
            saveConfig();
        } catch (Exception e) {
            e.printStackTrace();
            // Sorting Settings
            doSort = true;
            doSortAlphabetically = true;
            doKeepCursesBelow = true;
            // Coloring Settings
            doColorBooks = true;
            doColorOverrideWhenCursed = true; // TODO implement
            doColorBasedOnAlphabeticalOrder = true;
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
            // Sorting Settings
            properties.setProperty("sort", doSort + "");
            properties.setProperty("sort_alphabetically", doSortAlphabetically + "");
            properties.setProperty("keep_curses_below", doKeepCursesBelow + "");
            // Coloring Settings
            properties.setProperty("color_books", doColorBooks + "");
            properties.setProperty("override_curse_color", doColorOverrideWhenCursed + "");
            properties.setProperty("color_books_based_on_alphabetical_order", doColorBasedOnAlphabeticalOrder + "");
            saveEnchantmentData();
            properties.store(writer, null);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Sorting Settings
            doSort = true;
            doSortAlphabetically = true;
            doKeepCursesBelow = true;
            // Coloring Settings
            doColorBooks = true;
            doColorOverrideWhenCursed = true; // TODO implement
            doColorBasedOnAlphabeticalOrder = true;
            saveEnchantmentData();
        }
    }

    public static ConfigBuilder getConfigScreen() {
        // Base config builder
        ConfigBuilder builder = ConfigBuilder.create();
        // Creating categories
        ConfigCategory sortingCategory = builder.getOrCreateCategory("category.bebooks.sorting_settings");
        ConfigCategory bookColoring = builder.getOrCreateCategory("category.bebooks.book_coloring_settings");
        // Adding entries to the categories
        // Sorting settings page
        builder.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/spruce_planks.png"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        sortingCategory.addEntry(entryBuilder.startBooleanToggle("entry.bebooks.sorting_settings.sort", doSort).setSaveConsumer((doSortInput) -> {
            doSort = doSortInput;
        }).build());
        sortingCategory.addEntry(entryBuilder.startBooleanToggle("entry.bebooks.sorting_settings.sort_alphabetically", doSortAlphabetically).setSaveConsumer((doSortAlphabeticallyInput) -> {
            doSortAlphabetically = doSortAlphabeticallyInput;
        }).build());
        sortingCategory.addEntry(entryBuilder.startBooleanToggle("entry.bebooks.sorting_settings.keep_curses_at_bottom", doKeepCursesBelow).setSaveConsumer((doKeepCursesBelowInput) -> {
            doKeepCursesBelow = doKeepCursesBelowInput;
        }).build());
        // Coloring settings page
        bookColoring.addEntry(entryBuilder.startBooleanToggle("entry.bebooks.book_coloring_settings.active", doColorBooks).setSaveConsumer((doColorBooksInput) -> {
            doColorBooks = doColorBooksInput;
        }).build());
        bookColoring.addEntry(entryBuilder.startBooleanToggle("entry.bebooks.book_coloring_settings.do_color_based_on_alphabetical_order", doColorBasedOnAlphabeticalOrder).setSaveConsumer((doColorBasedOnAlphabeticalOrderInput) -> {
            doColorBasedOnAlphabeticalOrder = doColorBasedOnAlphabeticalOrderInput;
        }).build());
        bookColoring.addEntry(entryBuilder.startBooleanToggle("entry.bebooks.book_coloring_settings.curse_color_override_others", doColorOverrideWhenCursed).setSaveConsumer((doColorOverrideWhenCursedInput) -> {
            doColorOverrideWhenCursed = doColorOverrideWhenCursedInput;
        }).build());
        ArrayList<AbstractConfigListEntry> enchantments = new ArrayList<>();
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            String key = Registry.ENCHANTMENT.getId(enchantment).toString();
            enchantments.add(new StringColorEntry(enchantment.getTranslationKey(), StringUtils.getHexColorString(mappedEnchantmentColors.get(key)), (string) ->
            {
                EnchantmentData data = storedEnchantmentData.get(key);
                data.color = StringUtils.getValidIntColor(string);
                data.translatedName = data.translatedName.equals(Util.createTranslationKey("enchantment", new Identifier(key))) ? I18n.translate(data.translatedName) : data.translatedName;
                storedEnchantmentData.replace(key, data);
            }));
        }
        enchantments.sort(Comparator.comparing(entry -> I18n.translate(entry.getFieldName())));
        bookColoring.addEntry(entryBuilder.startSubCategory("subcategory.bebooks.book_coloring_settings.enchantment_color", enchantments).build());
        builder.setSavingRunnable(() -> {
            saveConfig();
            loadConfig();
        });
        return builder;
    }

    private static class EnchantmentData {
        public String translatedName;
        public int orderIndex;
        public int color;

        public EnchantmentData(String translatedName, int index, int color) {
            this.orderIndex = index;
            this.color = color;
            this.translatedName = translatedName;
        }
    }
}
