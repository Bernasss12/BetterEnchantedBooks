package dev.bernasss12.bebooks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.List;

public class BetterEnchantedBooks implements ModInitializer {
	@Override
	public void onInitialize() {
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? getColorFromEnchantmentList(stack) : -1, Items.ENCHANTED_BOOK);
	}

	public ListTag sortEnchantments(ItemStack stack){
		if(stack.isItemEqual(new ItemStack(Items.ENCHANTED_BOOK))){
			return EnchantedBookItem.getEnchantmentTag(stack);
		}else if(stack.hasEnchantments()){
			return stack.getEnchantments();
		}else{
			return new ListTag();
		}
	}

	private ListTag sortEnchantments(ListTag enchantments){
		List<EnchantmentData> enchantedDataList = NBTUtils.getEnchantmentData(enchantments);
		Collections.sort(enchantedDataList);
		ListTag sortedEnchantments = NBTUtils.getEnchantmentTag(enchantedDataList);
		return sortedEnchantments;
	}

	/* Get color associated with most valuable enchantment in the list enchantment. */
	public int getColorFromEnchantmentList(ItemStack stack){
		if(stack.isItemEqual(new ItemStack(Items.ENCHANTED_BOOK))){
			CompoundTag tag = stack.getTag();
			if(tag != null && tag.contains("EnchantmentColor")) {
						CompoundTag color = tag.getCompound("EnchantmentColor");
						return color.contains("color") ? color.getInt("color") : -1;
					}else return -1;
		}else return -1;
	}
}
