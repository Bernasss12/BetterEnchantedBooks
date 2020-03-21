package dev.bernasss12.bebooks.client.gui.config;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import dev.bernasss12.bebooks.EnchantmentData;
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
import net.minecraft.util.registry.Registry;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class BEBooksConfig {

    public static boolean LOADED = false;

    public static Map<String, EnchantmentData> ENCHANTMENT_DATA;
    public static Map<String, Integer> ENCHANTMENT_COLORS;
    public static Map<String, Integer> ENCHANTMENT_PRIORITY_ORDER;
    public static boolean SORT_ALPHABETICALLY = true;
    public static boolean COLOR_BOOKS = true;
    public static int DEFAULT_COLOR = 0x5c1500;

    public static void loadEnchantmentDataList() {
        File file = new File(FabricLoader.getInstance().getConfigDirectory(), "bebooks/enchantment_data.json");
        Gson gson = new Gson();
        // Try and read the file and parse the json.
        try {
            file.getParentFile().mkdirs();
            ENCHANTMENT_DATA = gson.fromJson(new InputStreamReader(new FileInputStream(file)), new TypeToken<Map<String, EnchantmentData>>() {
            }.getType());
            // After parsing the stored map information try and add any absent enchantments that may have been added since the last configuration of the mod.
            int index = ENCHANTMENT_DATA.size();
            for (Enchantment enchantment : Registry.ENCHANTMENT) {
                if (ENCHANTMENT_DATA.putIfAbsent(Objects.requireNonNull(Registry.ENCHANTMENT.getId(enchantment)).toString(), new EnchantmentData(I18n.translate(enchantment.getTranslationKey()), index, DEFAULT_COLOR)) == null)
                    index++;
            }
        } catch (Exception e) {
            // In case map parsing fails create a new empty map and populate it with all registered enchantments with the default color.
            System.err.println(e);
            ENCHANTMENT_DATA = new HashMap<>();
            int index = ENCHANTMENT_DATA.size();
            for (Enchantment enchantment : Registry.ENCHANTMENT) {
                //String translationKey = I18n.translate(enchantment.getTranslationKey());
                if (ENCHANTMENT_DATA.putIfAbsent(Objects.requireNonNull(Registry.ENCHANTMENT.getId(enchantment)).toString(), new EnchantmentData(I18n.translate(enchantment.getTranslationKey()), index, DEFAULT_COLOR)) == null)
                    index++;
            }
        }
        ENCHANTMENT_COLORS = new HashMap<>();
        ENCHANTMENT_PRIORITY_ORDER = new HashMap<>();
        ENCHANTMENT_DATA.forEach((key, enchantmentData) -> {
            ENCHANTMENT_COLORS.putIfAbsent(key, enchantmentData.color);
            ENCHANTMENT_PRIORITY_ORDER.putIfAbsent(key, enchantmentData.orderIndex);
        });
        saveEnchantmentData();
    }

    public static void saveEnchantmentData() {
        File file = new File(FabricLoader.getInstance().getConfigDirectory(), "bebooks/enchantment_data.json");
        Gson gson = new Gson();
        // Try and write the Map to the json config file.
        try {
            FileWriter writer = new FileWriter(file);
            gson.toJson(ENCHANTMENT_DATA, writer);
            writer.flush();
            writer.close();
        } catch (JsonIOException e) {
            // Upon failure print the exception.
            System.err.println(e);
        } catch (IOException e) {
            // Upon failure print the exception.
            System.err.println(e);
        }
    }

    public static void loadConfig() {
        File file = new File(FabricLoader.getInstance().getConfigDirectory(), "bebooks/config.properties");
        try {
            if (file.getParentFile().mkdirs()) System.out.println("[BEBooks] Config folder created!");
            SORT_ALPHABETICALLY = true;
            COLOR_BOOKS = true;
            loadEnchantmentDataList();
            if (!file.exists()) {
                saveConfig();
            }
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            SORT_ALPHABETICALLY = Boolean.parseBoolean(properties.getProperty("sort_alphabetically"));
            COLOR_BOOKS = Boolean.parseBoolean(properties.getProperty("color_books"));
            saveConfig();
        } catch (Exception e) {
            e.printStackTrace();
            SORT_ALPHABETICALLY = true;
            COLOR_BOOKS = true;
            loadEnchantmentDataList();
            try {
                Files.deleteIfExists(file.toPath());
            } catch (Exception ignored) {
            }
        }
        saveConfig();
        LOADED = true;
    }

    public static void saveConfig() {
        File file = new File(FabricLoader.getInstance().getConfigDirectory(), "bebooks/config.properties");
        try {
            FileWriter writer = new FileWriter(file, false);
            Properties properties = new Properties();
            properties.setProperty("sort_alphabetically", SORT_ALPHABETICALLY + "");
            properties.setProperty("color_books", COLOR_BOOKS + "");
            saveEnchantmentData();
            properties.store(writer, null);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            SORT_ALPHABETICALLY = true;
            COLOR_BOOKS = true;
            saveEnchantmentData();
        }
    }

    public static void flushEnchantedColors(){

    }

    public static ConfigBuilder getConfigScreen() {
        // Base config builder
        ConfigBuilder builder = ConfigBuilder.create();
        // Creating categories
        ConfigCategory mainCategory = builder.getOrCreateCategory("category.bebooks.base_settings");
        ConfigCategory bookColoring = builder.getOrCreateCategory("category.bebooks.book_coloring");
        // Adding entries to the categories
        // Main settings page
        builder.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/spruce_planks.png"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        mainCategory.addEntry(new StringColorEntry("entry.bebooks.base_settings.sort_alphabetically", StringUtils.getHexColorString(0x056677), new Consumer<String>() {
            @Override
            public void accept(String s) {

            }
        }));
        // Coloring settings page
        bookColoring.addEntry(entryBuilder.startBooleanToggle("entry.bebooks.book_coloring.active", COLOR_BOOKS).build());
        ArrayList<AbstractConfigListEntry> enchantments = new ArrayList<>();
        Map<String, String> editedColors = new HashMap<>();
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            String key = Registry.ENCHANTMENT.getId(enchantment).toString();
            enchantments.add(new StringColorEntry(enchantment.getTranslationKey(), StringUtils.getHexColorString(ENCHANTMENT_COLORS.get(key)), (string) ->
            {
                EnchantmentData data = ENCHANTMENT_DATA.get(key);
                data.color = StringUtils.getValidIntColor(string);
                ENCHANTMENT_DATA.replace(key, data);
                //ENCHANTMENT_COLORS.replace(key, StringUtils.getValidIntColor(string));
            }));
        }
        bookColoring.addEntry(entryBuilder.startSubCategory("subcategory.bebooks.enchantment_color", enchantments).build());
        builder.setSavingRunnable(() -> {
            flushEnchantedColors();
            saveConfig();
        });
        return builder;
    }
}
