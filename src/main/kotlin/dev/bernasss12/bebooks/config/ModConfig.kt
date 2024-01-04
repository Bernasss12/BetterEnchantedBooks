package dev.bernasss12.bebooks.config

import dev.bernasss12.bebooks.BetterEnchantedBooksLegacy
import dev.bernasss12.bebooks.client.gui.ModConfigLegacy.SortingSetting
import dev.bernasss12.bebooks.client.gui.ModConfigLegacy.TooltipSetting
import dev.bernasss12.bebooks.util.ModConstants.CONFIG_DIR
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_COLOR_BOOKS
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_COLOR_MODE
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_CURSE_COLOR_OVERRIDE
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_GLINT_SETTING
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_KEEP_CURSES_BELOW
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_SORTING_MODE
import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_TOOLTIP_MODE
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.io.path.createDirectories

object ModConfig {
    private val file: File = CONFIG_DIR.resolve("config.properties").toFile()
    private val properties: Properties = object : Properties() {
        override val values: MutableCollection<Any> = linkedSetOf()
    }

    // Sorting settings
    var sortingMode: SortingSetting
        get() = properties.getPropertyOrDefault("sorting_mode", DEFAULT_SORTING_MODE, SortingSetting::fromString)
        set(value) = properties.setProperty("sorting_mode", value)
    var keepCursesBelow: Boolean
        get() = properties.getPropertyOrDefault("keep_curses_below", DEFAULT_KEEP_CURSES_BELOW, String::toBoolean)
        set(value) = properties.setProperty("keep_curses_below", value)

    // Tooltip settings
    var showMaxEnchantmentLevel: Boolean
        get() = properties.getPropertyOrDefault("show_max_enchantment_level", DEFAULT_SHOW_ENCHANTMENT_MAX_LEVEL, String::toBoolean)
        set(value) = properties.setProperty("show_max_enchantment_level", value)
    var tooltipMode: TooltipSetting
        get() = properties.getPropertyOrDefault("tooltip_mode", DEFAULT_TOOLTIP_MODE, TooltipSetting::fromString)
        set(value) = properties.setProperty("tooltip_mode", value)


    // Coloring settings
    var colorBooks: Boolean
        get() = properties.getPropertyOrDefault("color_books", DEFAULT_COLOR_BOOKS, String::toBoolean)
        set(value) = properties.setProperty("color_books", value)
    var overrideCurseColor: Boolean
        get() = properties.getPropertyOrDefault("override_curse_color", DEFAULT_CURSE_COLOR_OVERRIDE, String::toBoolean)
        set(value) = properties.setProperty("override_curse_color", value)
    var colorMode: SortingSetting
        get() = properties.getPropertyOrDefault("color_mode", DEFAULT_COLOR_MODE, SortingSetting::fromString)
        set(value) = properties.setProperty("color_mode", value)

    // Remove enchantment glint
    var enchantedBookGlint: Boolean
        get() = properties.getPropertyOrDefault("enchanted_book_glint", DEFAULT_GLINT_SETTING, String::toBoolean)
        set(value) = properties.setProperty("enchanted_book_glint", value)

    fun load() {
        if (file.exists()) {
            try {
                file.inputStream().use { stream ->
                    properties.load(stream)
                }
            } catch (e: IOException) {
                BetterEnchantedBooksLegacy.LOGGER.warn("Could not read ${file.name} properties file. Using defaults.")
            }
        }
    }

    fun save() {
        try {
            CONFIG_DIR.createDirectories()
            properties.apply {
                file.outputStream().use { stream ->
                    try {
                        // TODO add client command to reload settings
                        store(stream, "The settings will only be loaded at game start when changed here.")
                        BetterEnchantedBooksLegacy.LOGGER.debug("Saving configs to disk.")
                    } catch (e: IOException) {
                        BetterEnchantedBooksLegacy.LOGGER.warn("Could not write ${file.name} properties file. Changed settings may be lost.")
                    }
                }
            }
        } catch (e: IOException) {
            BetterEnchantedBooksLegacy.LOGGER.error("Couldn't create config directory.\nChanged settings could be lost!", e)
        }
    }

    private fun <T : Any> Properties.getPropertyOrDefault(key: String, default: T, convert: (String) -> T): T {
        return convert.invoke(
            getOrPut(key) { default.toString() }.toString()
        )
    }

    private fun Properties.setProperty(key: String, any: Any): Unit {
        setProperty(key, any.toString())
    }
}