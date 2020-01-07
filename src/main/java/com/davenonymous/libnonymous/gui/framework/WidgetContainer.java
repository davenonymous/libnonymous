package com.davenonymous.libnonymous.gui.framework;

import com.davenonymous.libnonymous.Libnonymous;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class WidgetContainer extends Container {
    public static ResourceLocation SLOTGROUP_PLAYER = new ResourceLocation(Libnonymous.MODID, "player_slots");

    private IItemHandler playerInventory;

    protected WidgetContainer(@Nullable ContainerType<?> type, int id, PlayerInventory inv) {
        super(type, id);

        this.playerInventory = new InvWrapper(inv);
    }

    @Override
    protected Slot addSlot(Slot slotIn) {
        if(!(slotIn instanceof WidgetSlot)) {
            throw new RuntimeException("Only WidgetSlots are allowed in a WidgetContainer!");
        }
        return super.addSlot(slotIn);
    }

    protected void lockSlot(int index) {
        Slot slot = this.inventorySlots.get(index);
        if(slot instanceof WidgetSlot) {
            ((WidgetSlot) slot).setLocked(true);
            this.inventorySlots.set(index, slot);
        }
    }

    private int addSlotRange(ResourceLocation id, IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            this.addSlot(new WidgetSlot(id, handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(ResourceLocation id, IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = this.addSlotRange(id, handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    protected void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        this.addSlotBox(SLOTGROUP_PLAYER, playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        this.addSlotRange(SLOTGROUP_PLAYER, playerInventory, 0, leftCol, topRow, 9, 18);
    }

    // We are relying on the client to tell the server which slots are currently enabled,
    // see MessageEnabledSlots.
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);
        if(slot == null || !slot.getHasStack()) {
            return ItemStack.EMPTY;
        }

        if(index >= 0 && index <= 35) {
            // Player slot
            int firstValidSlot = -1;
            int lastValidSlot = -1;
            int slotId = 0;
            for(Slot invSlot : this.inventorySlots) {
                if(invSlot instanceof WidgetSlot && invSlot.isEnabled() && invSlot.getStack().getCount() < invSlot.getStack().getMaxStackSize()) {
                    if(firstValidSlot == -1) {
                        firstValidSlot = slotId;
                    }

                    lastValidSlot = slotId;
                } else {
                    if(lastValidSlot != -1) {
                        break;
                    }
                }

                slotId++;
            }

            if(firstValidSlot == -1 || lastValidSlot == -1) {
                return ItemStack.EMPTY;
            }

            ItemStack clickedStack = slot.getStack();
            if(!this.mergeItemStack(clickedStack, firstValidSlot, lastValidSlot+1, index,false)) {
                return ItemStack.EMPTY;
            }

            slot.onSlotChanged();
            return clickedStack;
        } else if(index > 35) {
            // Inventory slot
            ItemStack clickedStack = slot.getStack();
            if(clickedStack.getCount() > clickedStack.getMaxStackSize()) {
                ItemStack shrinkedStack = clickedStack.copy();
                shrinkedStack.setCount(clickedStack.getMaxStackSize());

                if(!this.mergeItemStack(shrinkedStack, 0, 36, index, false)) {
                    return ItemStack.EMPTY;
                }

                ItemStack remainder = slot.getStack();
                remainder.setCount(remainder.getCount() - remainder.getMaxStackSize());

                if(!shrinkedStack.isEmpty()) {
                    remainder.setCount(remainder.getCount() + shrinkedStack.getCount());
                }
                slot.putStack(remainder);

                return ItemStack.EMPTY;
            } else {
                if(!this.mergeItemStack(clickedStack, 0, 35, index, false)) {
                    return ItemStack.EMPTY;
                }

                if(clickedStack.isEmpty()) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
                }

                return clickedStack;
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * Merges provided ItemStack with the first avaliable one in the container/player inventor between minIndex
     * (included) and maxIndex (excluded). Args : stack, minIndex, maxIndex, negativDirection. /!\ the Container
     * implementation do not check if the item is valid for the slot
     */
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, int skipIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while(!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                if(i == skipIndex) {
                    if (reverseDirection) {
                        --i;
                    } else {
                        ++i;
                    }
                    continue;
                }

                Slot slot = this.inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();
                if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.onSlotChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.onSlotChanged();
                        flag = true;
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();
                if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
                    if (stack.getCount() > slot1.getSlotStackLimit()) {
                        slot1.putStack(stack.split(slot1.getSlotStackLimit()));
                    } else {
                        slot1.putStack(stack.split(stack.getCount()));
                    }

                    slot1.onSlotChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
