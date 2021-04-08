package dev.bernasss12.bebooks.util;

import dev.bernasss12.bebooks.client.gui.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.bernasss12.bebooks.BetterEnchantedBooks.LOGGER;

@Environment(EnvType.CLIENT)
public final class NBTUtils {

    public static ListTag sort(ListTag listTag, ModConfig.SortingSetting mode, boolean cursesBelow) {
        Comparator<EnchantmentCompound> comparator;

        if (cursesBelow) {
            comparator = Comparator.comparing(EnchantmentCompound::isCursed);
        } else {
            comparator = Comparator.comparing(e -> 0); // Preserve existing order
        }

        switch (mode) {
            case ALPHABETICALLY:
                comparator = comparator.thenComparing(EnchantmentCompound::getTranslatedName);
                break;
            case CUSTOM:
                comparator = comparator.thenComparing(EnchantmentCompound::getIndex);
                break;
            case DISABLED:
                // Can still sort by isCursed
                break;
        }

        ListTag result = new ListTag();
        listTag.stream().map(EnchantmentCompound::new).sorted(comparator)
            .forEachOrdered(tag -> result.add(tag.asCompoundTag()));
        return result;
    }

    public static boolean hasCurses(ListTag listTag) {
        return listTag.stream().map(EnchantmentCompound::new).anyMatch(EnchantmentCompound::isCursed);
    }

    public static String getPriorityEnchantmentId(ListTag listTag, ModConfig.SortingSetting mode) {
        ListTag sorted = sort(listTag, mode, true);
        if (sorted.isEmpty()) return "";

        if (ModConfig.doCurseColorOverride && hasCurses(listTag)) {
            return listTag.getCompound(listTag.size() - 1).getString("id");
        }
        return sorted.getCompound(0).getString("id");
    }

    private static class EnchantmentCompound {
        @NotNull private final CompoundTag compound;
        @NotNull private final String id;
        @NotNull private final String translatedName;
        private final int index;
        private final boolean isCursed;

        EnchantmentCompound(@NotNull Tag tag) {
            if (tag.getType() != 10) {
                throw new AssertionError("tag is not a CompoundTag");
            }

            this.compound = (CompoundTag) tag;
            this.id = compound.getString("id");
            int lvl = compound.getShort("lvl");

            Enchantment enchantment = Objects.requireNonNull(Registry.ENCHANTMENT.get(new Identifier(id)));
            this.isCursed = enchantment.isCursed();

            ModConfig.EnchantmentData enchantmentData = ModConfig.enchantmentDataMap.get(id);
            if (enchantmentData != null) {
                this.translatedName = enchantmentData.translatedName;
                this.index = enchantmentData.orderIndex;
            } else {
                this.translatedName = enchantment.getName(lvl).asString();
                this.index = ModConfig.enchantmentDataMap.size();
            }
        }

        @NotNull
        public CompoundTag asCompoundTag() {
            return compound;
        }

        public boolean isCursed() {
            return isCursed;
        }

        @NotNull
        public String getTranslatedName() {
            return translatedName;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return "id:\"" + id + "\",index:" + index + (isCursed ? ",curse" : "");
        }
    }
}
