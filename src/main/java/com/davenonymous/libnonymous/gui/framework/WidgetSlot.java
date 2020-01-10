package com.davenonymous.libnonymous.gui.framework;

import com.davenonymous.libnonymous.gui.framework.event.VisibilityChangedEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.gui.framework.widgets.Widget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class WidgetSlot extends SlotItemHandler {
    private boolean enabled;
    private ResourceLocation id;
    private boolean locked = false;

    public WidgetSlot(ResourceLocation slotId, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);

        this.id = slotId;
        this.enabled = false;
    }

    public void bindToWidget(Widget widget) {
        widget.addListener(VisibilityChangedEvent.class, (event, widget1) -> {
            this.setEnabled(event.newValue && widget.areAllParentsVisible());
            return WidgetEventResult.CONTINUE_PROCESSING;
        });
    }

    public ResourceLocation getGroupId() {
        return this.id;
    }

    public boolean matches(ResourceLocation slotId) {
        return this.id.equals(slotId);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public WidgetSlot setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public WidgetSlot setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    @Override
    public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
        if(locked) {
            return stack;
        }

        if(stack.getCount() <= stack.getMaxStackSize()) {
            return super.onTake(thePlayer, stack);
        }

        int total = stack.getCount() + getStack().getCount();
        ItemStack before = stack.copy();
        ItemStack after = before.copy();
        after.setCount(total - before.getMaxStackSize());

        stack.setCount(before.getMaxStackSize());
        this.putStack(after);
        this.onSlotChanged();

        return stack;
    }

    @Override
    public boolean canTakeStack(PlayerEntity player) {
        if(locked) {
            return false;
        }

        if(player != null) {
            ItemStack mouseStack = player.inventory.getItemStack();
            if(mouseStack.isEmpty()) {
                return true;
            }

            if(getStack().getCount() > getStack().getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        return 64;
    }
}
