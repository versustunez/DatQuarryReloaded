package dev.vstz.datquarry.storage

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class FakeItemStack(val item: Item) {
    var amount: Int = 0
    fun addAmount(amount: Int): Int {
        this.amount += amount
        return amount;
    }

    fun removeAmount(amount: Int): Int {
        val removal = minOf(amount, this.amount);
        this.amount -= removal;
        return removal;
    }

    fun removeSimulated(amount: Int): Int {
        val removal = minOf(amount, this.amount, ItemStack(item, this.amount).maxStackSize);
        return removal;
    }

    fun asItemStack(): ItemStack {
        if (this.amount == 0) {
            return ItemStack.EMPTY;
        }
        val stack = ItemStack(item, this.amount)
        stack.count = maxOf(this.amount, stack.maxStackSize)
        return stack
    }

    fun copyItemStackWithAmount(amount: Int): ItemStack {
        val stack = asItemStack()
        stack.count = minOf(amount, this.amount)
        if (amount == 0)
            return ItemStack.EMPTY

        return stack
    }

    fun asUnlimitedStack(): ItemStack {
        if (this.amount == 0) {
            return ItemStack.EMPTY;
        }
        val stack = ItemStack(item, this.amount)
        return stack
    }
}