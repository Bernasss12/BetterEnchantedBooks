package dev.bernasss12.bebooks.mixin;

import dev.bernasss12.bebooks.EnchantmentData;
import dev.bernasss12.bebooks.NBTUtils;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(EnchantedBookItem.class)
public abstract class EnchantedBookMixin extends Item {

	public EnchantedBookMixin(Settings settings) {
		super(settings);
	}

	@Inject(at = @At("TAIL"), method = "addEnchantment")
	private static void addEnchantment(ItemStack stack, InfoEnchantment enchantmentInfo, CallbackInfo info) {
		CompoundTag tag = stack.getTag();
		int colorInt = 0xff0000;
		if(tag.contains("StoredEnchantments")){
			ListTag enchantmentList = tag.getList("StoredEnchantments", 10);
			List<EnchantmentData> enchData = NBTUtils.getEnchantmentData(enchantmentList);
			Collections.sort(enchData);
			colorInt = enchData.get(0).color;
			ListTag listTag = NBTUtils.getEnchantmentTag(enchData);
			stack.getOrCreateTag().put("StoredEnchantments", listTag);
			CompoundTag color = new CompoundTag();
			color.putInt("color", colorInt);
			stack.getOrCreateTag().put("EnchantmentColor", color);
		}
	}
}
