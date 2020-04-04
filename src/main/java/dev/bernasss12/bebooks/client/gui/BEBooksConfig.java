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
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class BEBooksConfig {


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

    // TODO Possibly will be an Enum with 3 different options in the future. (doSort and doSortAlphabetically -> EnumSortingMode(NONE, ALPHABETICAL, PRIORITY)
    // Sorting Settings
    public static boolean doSort = true;
    public static boolean doSortAlphabetically = true;
    public static boolean doKeepCursesBelow = true;
    // Coloring Settings
    public static boolean doColorBooks = true;
    public static boolean doColorOverrideWhenCursed = true;
    public static boolean doColorBasedOnAlphabeticalOrder = true;

    // Tooltip Icon Settings
    public static TooltipSetting tooltipSetting = TooltipSetting.ENABLED;

    // Default minecraft book color, sorta
    public static int defaultBookStripColor = 0xc5133a;

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
            if (storedEnchantmentData.putIfAbsent(id, new EnchantmentData(I18n.translate(enchantment.getTranslationKey()), index, defaultBookStripColor)) == null)
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
        try {
            if (file.getParentFile().mkdirs()) System.out.println("[BEBooks] Config folder created!");
            // ThreadLocal
            // Sorting Settings
            doSort = true;
            doSortAlphabetically = true;
            doKeepCursesBelow = true;
            // Coloring Settings
            doColorBooks = true;
            doColorOverrideWhenCursed = true; // TODO implement
            doColorBasedOnAlphabeticalOrder = true;
            // Tooltip Settings
            tooltipSetting = DEFAULT_TOOLTIP_SETTING;
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
            // Tooltip Settings
            tooltipSetting = TooltipSetting.fromString(properties.getProperty("tooltip_mode"));
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
            // Sorting Settings
            properties.setProperty("sort", doSort + "");
            properties.setProperty("sort_alphabetically", doSortAlphabetically + "");
            properties.setProperty("keep_curses_below", doKeepCursesBelow + "");
            // Coloring Settings
            properties.setProperty("color_books", doColorBooks + "");
            properties.setProperty("override_curse_color", doColorOverrideWhenCursed + "");
            properties.setProperty("color_books_based_on_alphabetical_order", doColorBasedOnAlphabeticalOrder + "");
            // Tooltip Settings
            tooltipSetting = DEFAULT_TOOLTIP_SETTING;
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
        ConfigCategory tooltipCategory = builder.getOrCreateCategory("category.bebooks.tooltip_settings");
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
        // Tooltip settings page
        tooltipCategory.addEntry(entryBuilder.startEnumSelector("entry.bebooks.tooltip_settings.tooltip_mode", TooltipSetting.class, tooltipSetting).setDefaultValue(DEFAULT_TOOLTIP_SETTING).setSaveConsumer( setting -> tooltipSetting = setting ).build());
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

        public static TooltipSetting fromString(String string){
            for(TooltipSetting value : TooltipSetting.values()){
                if(value.toString().equals(string)){
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

    public static class EnchantmentData {
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
