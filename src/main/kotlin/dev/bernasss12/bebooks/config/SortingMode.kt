package dev.bernasss12.bebooks.config

import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_SORTING_MODE
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry.Translatable
import java.util.*

enum class SortingMode: Translatable {
    ALPHABETICALLY,
    CUSTOM,
    DISABLED;

    companion object {
        fun fromString(string: String): SortingMode {
            for (value in SortingMode.entries) {
                if (value.toString() == string) {
                    return value
                }
            }
            return DEFAULT_SORTING_MODE
        }
    }

    override fun getKey(): String {
        return "enum.bebooks.sorting_settings." + toString().lowercase(Locale.getDefault())
    }
}