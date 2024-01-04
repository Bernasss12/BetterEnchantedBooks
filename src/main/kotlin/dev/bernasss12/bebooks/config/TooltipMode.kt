package dev.bernasss12.bebooks.config

import dev.bernasss12.bebooks.util.ModConstants.DEFAULT_TOOLTIP_MODE
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry.Translatable
import java.util.*

enum class TooltipMode : Translatable {
    ENABLED,
    ON_SHIFT,
    DISABLED;

    override fun getKey(): String {
        return "enum.bebooks.tooltip_settings." + toString().lowercase(Locale.getDefault())
    }

    companion object {
        fun fromString(string: String): TooltipMode {
            for (value in entries) {
                if (value.toString() == string) {
                    return value
                }
            }
            return DEFAULT_TOOLTIP_MODE
        }
    }
}