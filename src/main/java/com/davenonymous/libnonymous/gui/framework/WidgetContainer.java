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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WidgetContainer extends Container {
    public static ResourceLocation SLOTGROUP_PLAYER = new ResourceLocation(Libnonymous.MODID, "player_slots");

    private IItemHandler playerInventory;
    private CircularPointedArrayList<ResourceLocation> slotGroups;
    private Map<ResourceLocation, List<Integer>> slotGroupMap;

    private int nextSlotId = 0;
    protected WidgetContainer(@Nullable ContainerType<?> type, int id, PlayerInventory inv) {
        super(type, id);

        this.playerInventory = new InvWrapper(inv);
        this.slotGroups = new CircularPointedArrayList<>();
        this.slotGroupMap = new HashMap<>();
    }

    @Override
    protected Slot addSlot(Slot slotIn) {
        if(!(slotIn instanceof WidgetSlot)) {
            throw new RuntimeException("Only WidgetSlots are allowed in a WidgetContainer!");
        }

        ResourceLocation slotGroupId = ((WidgetSlot)slotIn).getGroupId();
        if(!this.slotGroups.contains(slotGroupId)) {
            this.slotGroups.add(slotGroupId);
        }

        if(!this.slotGroupMap.containsKey(slotGroupId)) {
            this.slotGroupMap.put(slotGroupId, new ArrayList<>());
        }

        this.slotGroupMap.get(slotGroupId).add(nextSlotId++);
        return super.addSlot(slotIn);
    }

    protected void lockSlot(int index) {
        Slot slot = this.inventorySlots.get(index);
        if(slot instanceof WidgetSlot) {
            ((WidgetSlot) slot).setLocked(true);
            this.inventorySlots.set(index, slot);
        }
    }

    protected int addSlotRange(ResourceLocation id, IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            this.addSlot(new WidgetSlot(id, handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    protected int addSlotBox(ResourceLocation id, IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
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

    private ArrayList<Integer> getTransferTargetSlots(ItemStack stack) {
        ArrayList<Integer> result = new ArrayList<>();
        for(int groupIndex = 0; groupIndex < this.slotGroups.size(); groupIndex++) {
            ResourceLocation targetGroup = this.slotGroups.next();
            List<Integer> slotsForThisGroup = this.slotGroupMap.get(targetGroup);
            for(int slotIndex : slotsForThisGroup) {
                WidgetSlot slot = (WidgetSlot) this.inventorySlots.get(slotIndex);
                if(!slot.isEnabled() || slot.isLocked()) {
                    continue;
                }

                if(!slot.isItemValid(stack)) {
                    continue;
                }

                if(slot.getHasStack()) {
                    if(!stack.isStackable()) {
                        continue;
                    }

                    ItemStack existingStack = slot.getStack();
                    if(!existingStack.isStackable()) {
                        continue;
                    }

                    if(existingStack.getCount() >= existingStack.getMaxStackSize()) {
                        continue;
                    }

                    if(existingStack.getCount() >= slot.getSlotStackLimit()) {
                        continue;
                    }

                    if(existingStack.getCount() >= slot.getItemStackLimit(existingStack)) {
                        continue;
                    }

                    if(!areItemsAndTagsEqual(stack, existingStack)) {
                        continue;
                    }
                }

                result.add(slotIndex);
            }
        }

        return result;
    }

    private int getSlotStackLimit(WidgetSlot slot, ItemStack stack) {
        int limit = Integer.MAX_VALUE;
        limit = Math.min(limit, slot.getItemStackLimit(stack));
        limit = Math.min(limit, slot.getSlotStackLimit());
        limit = Math.min(limit, stack.getMaxStackSize());
        return limit;
    }

    // This method assumes that the widget slot already fulfills all required conditions.
    // See the getTransferTargetSlots method above.
    private ItemStack insertStackIntoSlot(WidgetSlot slot, ItemStack stack) {
        ItemStack existingStack = slot.getStack();
        int fitSize = getSlotStackLimit(slot, stack);
        int remainingSpace = fitSize - existingStack.getCount();
        int toAddSize = stack.getCount();
        int remaining = Math.max(0, toAddSize - remainingSpace);
        int inserted = toAddSize - remaining;

        ItemStack toInsert = stack.copy();
        toInsert.setCount(inserted + existingStack.getCount());
        slot.putStack(toInsert);

        ItemStack remainingStack = stack.copy();
        remainingStack.setCount(remaining);
        return remainingStack;
    }

    // We are relying on the client to tell the server which slots are currently enabled,
    // see MessageEnabledSlots.
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        Slot uncastSlot = this.inventorySlots.get(index);
        if(uncastSlot == null || !uncastSlot.getHasStack() || !(uncastSlot instanceof WidgetSlot)) {
            return ItemStack.EMPTY;
        }
        WidgetSlot slot = (WidgetSlot)uncastSlot;

        ItemStack stackToMove = uncastSlot.getStack().copy();
        if(stackToMove.isEmpty()) {
            return ItemStack.EMPTY;
        }

        this.slotGroups.setPointerTo(slot.getGroupId());
        List<Integer> availableSlotsInOrderOfPriority = getTransferTargetSlots(slot.getStack());
        for(int targetSlotId : availableSlotsInOrderOfPriority) {
            if(targetSlotId == index) {
                // Skip own slot
                continue;
            }

            WidgetSlot targetSlot = (WidgetSlot) this.inventorySlots.get(targetSlotId);
            stackToMove = insertStackIntoSlot(targetSlot, stackToMove);
            if(stackToMove.isEmpty()) {
                break;
            }
        }

        slot.putStack(stackToMove);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
