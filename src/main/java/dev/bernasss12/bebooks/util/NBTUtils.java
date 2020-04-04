package dev.bernasss12.bebooks.util;

import dev.bernasss12.bebooks.client.gui.BEBooksConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public final class NBTUtils {

    public static ListTag sort(ListTag listTag, BEBooksConfig.SortingSetting mode) throws Exception {
        return sort(listTag, mode, BEBooksConfig.doKeepCursesBelow);
    }

    /**
     * Turn a ListTag of enchantments into an ArrayList of enchantment data
     *
     * @param mode true for Alphabelical sorting, false for priority index sorting.
     **/
    public static ListTag sort(ListTag listTag, BEBooksConfig.SortingSetting mode, boolean cursesBelow) throws Exception {
        List<EnchantmentCompound> sortedEnchantments = fromListTag(listTag);
        // Sorting
        switch (mode) {
            case ALPHABETICALLY:
                try {
                    sortedEnchantments.sort(Comparator.comparing(EnchantmentCompound::getTranslatedName));
                } catch (Exception e) {
                    System.out.println("Failed to sort ListTag alphabetically: " + listTag);
                    System.out.println(sortedEnchantments);
                    throw new Exception(e);
                }
                break;
            case CUSTOM:
                try {
                    sortedEnchantments.sort(Comparator.comparing(EnchantmentCompound::getIndex));
                } catch (Exception e) {
                    System.out.println("Failed to sort ListTag via priority index: " + listTag);
                    System.out.println(sortedEnchantments);
                    throw new Exception(e);
                }
                break;
            case DISABLED:
                break;
        }
        // Curse filtering
        if (cursesBelow) {
            List<EnchantmentCompound> curses = new ArrayList<>();
            // Checks individually for enchantments that are curses.
            sortedEnchantments.forEach((enchantment) -> {
                if (enchantment.isCursed()) {
                    curses.add(enchantment);
                }
            });
            // Remove curses from wherever they are and place them still sorted, but at the bottom.
            sortedEnchantments.removeAll(curses);
            sortedEnchantments.addAll(curses);
        }
        return toListTag(sortedEnchantments);
    }

    public static boolean hasCurses(ListTag listTag) {
        List<EnchantmentCompound> result = fromListTag(listTag).stream().filter(EnchantmentCompound::isCursed).collect(Collectors.toList());
        return !result.isEmpty();
    }

    public static String getPriorityEnchantmentId(ListTag listTag, BEBooksConfig.SortingSetting mode) {
        try {
            List<EnchantmentCompound> enchantmentCompounds = fromListTag(sort(listTag, mode, true));
            if (BEBooksConfig.doCurseColorOverride && hasCurses(listTag)) {
                return ((CompoundTag) listTag.get(listTag.size() - 1)).getString("id");
            }
            return !enchantmentCompounds.isEmpty() ? enchantmentCompounds.get(0).id : "";
        } catch (Exception e) {
            return "";
        }
    }

    public static List<EnchantmentCompound> fromListTag(ListTag listTag) {
        List<EnchantmentCompound> result = new ArrayList<>();
        listTag.forEach(tag -> result.add(new EnchantmentCompound((CompoundTag) tag)));
        return result;
    }

    public static ListTag toListTag(List<EnchantmentCompound> compounds) {
        ListTag result = new ListTag();
        compounds.forEach(compound -> result.add(compound.toCompoundTag()));
        return result;
    }

    private static class EnchantmentCompound {
        private String id;
        private String translatedName;
        private int lvl;
        private int index;
        private boolean isCursed;

        EnchantmentCompound(CompoundTag compound) {
            this.id = compound.getString("id");
            this.lvl = compound.getShort("lvl");
            this.isCursed = Objects.requireNonNull(Registry.ENCHANTMENT.get(new Identifier(id))).isCursed();
            if (BEBooksConfig.storedEnchantmentData.containsKey(id)) {
                this.translatedName = BEBooksConfig.storedEnchantmentData.get(id).translatedName;
                this.index = BEBooksConfig.storedEnchantmentData.get(id).orderIndex;
            } else {
                this.translatedName = Registry.ENCHANTMENT.get(new Identifier(id)).getName(lvl).asString();
                this.index = BEBooksConfig.storedEnchantmentData.size();
            }
        }

        public CompoundTag toCompoundTag() {
            CompoundTag enchantmentCompound = new CompoundTag();
            enchantmentCompound.putString("id", this.id);
            enchantmentCompound.putShort("lvl", (short) this.lvl);
            return enchantmentCompound;
        }

        public boolean isCursed() {
            return isCursed;
        }

        public String getTranslatedName() {
            return this.translatedName;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public String toString() {
            return "id:\"" + id + "\",index:" + index + (isCursed ? ",curse" : "");
        }
    }
}
