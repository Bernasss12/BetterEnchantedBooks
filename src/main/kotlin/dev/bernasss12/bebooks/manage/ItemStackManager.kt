package dev.bernasss12.bebooks.manage

import net.minecraft.item.ItemStack

object ItemStackManager {
    private val currentItemstack = ThreadLocal.withInitial { ItemStack.EMPTY }

    @JvmStatic
    fun setItemstack(stack: ItemStack) { currentItemstack.set(stack) }

    @JvmStatic
    fun getItemstack(): ItemStack = currentItemstack.get()
}