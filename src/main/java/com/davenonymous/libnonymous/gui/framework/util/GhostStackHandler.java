/*
package com.davenonymous.libnonymous.gui.framework.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class GhostStackHandler implements IItemHandler, IItemHandlerModifiable {
    protected ItemStack stack = ItemStack.EMPTY;
    private boolean limitToOne;

    public GhostStackHandler() {
        this(true);
    }

    public GhostStackHandler(boolean limitToOne) {
        this.limitToOne = limitToOne;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.stack;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (simulate) {
            return stack;
        }

        if (!isItemValid(slot, stack)) {
            return stack;
        }

        if (stack.isEmpty()) {
            this.stack = ItemStack.EMPTY;
            return ItemStack.EMPTY;
        }

        ItemStack newStack = limitToOne ? ItemHandlerHelper.copyStackWithSize(stack, 1) : stack.copy();
        this.stack = newStack;

        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return limitToOne ? 1 : 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        this.stack = limitToOne ? ItemHandlerHelper.copyStackWithSize(stack, 1) : stack.copy();
    }
}
*/