package dev.bernasss12.bebooks.mixin;

import dev.bernasss12.bebooks.EnchantmentData;
import dev.bernasss12.bebooks.NBTUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin{

	public ItemStackMixin(Item item) {
		super();
	}

	@Inject(at = @At("HEAD"), method = "appendEnchantments")
	private static void appendEnchantmentsHead(List<Text> tooltip, ListTag enchantments, CallbackInfo info) {
		List<EnchantmentData> enchantmentData = NBTUtils.getEnchantmentData(enchantments);
		Collections.sort(enchantmentData);
		ListTag sortedEnchantments = NBTUtils.getEnchantmentListTag(enchantmentData);
		enchantments.clear();
		for(int i = 0; i < sortedEnchantments.size(); i++){
			enchantments.add(sortedEnchantments.get(i));
		}
	}
}
