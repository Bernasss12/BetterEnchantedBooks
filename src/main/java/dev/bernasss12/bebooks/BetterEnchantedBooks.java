package dev.bernasss12.bebooks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.registry.Registry;

public class BetterEnchantedBooks implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? getColorFromEnchantmentList(stack) : -1, Items.ENCHANTED_BOOK);
		Registry.ENCHANTMENT.iterator();
	}
	
	/* Get color associated with most valuable enchantment in the list enchantment. */
	public int getColorFromEnchantmentList(ItemStack stack) {
		if (stack.isItemEqual(new ItemStack(Items.ENCHANTED_BOOK))) {
			ListTag enchantments = EnchantedBookItem.getEnchantmentTag(stack);
			CompoundTag firstEnchantment = enchantments.getCompound(0);
			switch (firstEnchantment.getString("id")) {
				case "minecraft:mending":
					return 0x0000fe;
				case "minecraft:fortune":
					return 0xfe2020;
				case "minecraft:silk_touch":
					return 0x15fe15;
				default:
					return 0x5e0000;
			}
		} else return -1;
	}
}
