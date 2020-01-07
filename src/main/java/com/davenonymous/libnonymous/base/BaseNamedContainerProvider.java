package com.davenonymous.libnonymous.base;

import com.davenonymous.libnonymous.utils.IThreeToOneSupplier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class BaseNamedContainerProvider implements INamedContainerProvider {
    ITextComponent displayName;
    IThreeToOneSupplier<Integer, PlayerInventory, PlayerEntity, Container> supplier;

    public BaseNamedContainerProvider(ITextComponent displayName, IThreeToOneSupplier<Integer, PlayerInventory, PlayerEntity, Container> supplier) {
        this.displayName = displayName;
        this.supplier = supplier;
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.displayName;
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
        return this.supplier.apply(id, inv, player);
    }
}
