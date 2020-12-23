package com.davenonymous.libnonymous.base;

import com.davenonymous.libnonymous.serialization.FieldUtils;
import com.davenonymous.libnonymous.serialization.nbt.NBTFieldSerializationData;
import com.davenonymous.libnonymous.serialization.Store;
import com.davenonymous.libnonymous.serialization.nbt.NBTFieldUtils;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class BaseTileEntity extends TileEntity implements ITickableTileEntity {
    private boolean initialized = false;

    @Store(storeWithItem = true, sendInUpdatePackage = true)
    protected String customName;

    @Store(storeWithItem = true, sendInUpdatePackage = true)
    protected UUID owner;

    @Store(sendInUpdatePackage = true)
    private int incomingRedstonePower = 0;

    private List<NBTFieldSerializationData> NBTActions;

    public BaseTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);

        this.NBTActions = NBTFieldUtils.initSerializableStoreFields(this.getClass());
    }

    public void loadFromItem(ItemStack stack) {
        if(!stack.hasTag()) {
            return;
        }

        NBTFieldUtils.readFieldsFromNBT(NBTActions, this, stack.getTag(), data -> data.storeWithItem);
        this.markDirty();
    }

    public void saveToItem(ItemStack stack) {
        CompoundNBT compound = createItemStackTagCompound();
        stack.setTag(compound);
    }

    protected CompoundNBT createItemStackTagCompound() {
        return NBTFieldUtils.writeFieldsToNBT(NBTActions, this, new CompoundNBT(), data -> data.storeWithItem);
    }

    public void notifyClients() {
        world.notifyBlockUpdate(this.pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return NBTFieldUtils.writeFieldsToNBT(NBTActions, this, super.getUpdateTag(), data -> data.sendInUpdatePackage);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        NBTFieldUtils.readFieldsFromNBT(NBTActions, this, pkt.getNbtCompound(), data -> data.sendInUpdatePackage);

        /*
        // TODO: This should not be generalized in this way as it triggers on changes to blocks not belonging to this gui.
        if(world.isRemote && Minecraft.getMinecraft().currentScreen instanceof WidgetContainerScreen) {
            ((WidgetContainerScreen) Minecraft.getMinecraft().currentScreen).fireDataUpdateEvent();
        }
        */
    }

    public void read(CompoundNBT compound) {
        super.read(getBlockState(), compound);

        NBTFieldUtils.readFieldsFromNBT(NBTActions, this, compound, data -> true);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);
        compound = NBTFieldUtils.writeFieldsToNBT(NBTActions, this, compound, data -> true);

        return compound;
    }

    @Override
    public void tick() {
        if (!this.getWorld().isRemote && !this.initialized) {
            initialize();
            this.initialized = true;
        }
    }

    protected void initialize() {
    }

    protected void spawnItem(ItemStack stack) {
        ItemEntity entityItem = new ItemEntity(world, getPos().getX()+0.5f, getPos().getY()+0.7f, getPos().getZ()+0.5f, stack);
        entityItem.lifespan = 1200;
        entityItem.setPickupDelay(5);

        entityItem.setMotion(0.0f, 0.10f, 0.0f);

        world.addEntity(entityItem);
    }


    /**
     * Called when the block stops receiving a redstone signal.
     */
    public void redstonePulse() {

    }

    public void redstoneChanged(int previous, int now) {

    }


    public int getRedstonePowerFromNeighbors() {
        return this.world.getRedstonePowerFromNeighbors(this.pos);
    }

    public int getIncomingRedstonePower() {
        return incomingRedstonePower;
    }

    public BaseTileEntity setIncomingRedstonePower(int incomingRedstonePower) {
        this.incomingRedstonePower = incomingRedstonePower;
        return this;
    }

    public boolean hasCustomName() {
        return customName != null && customName.length() > 0;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return world.getServer().getPlayerProfileCache().getProfileByUUID(getOwner()).getName();
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setOwner(PlayerEntity player) {
        if(player == null) {
            return;
        }

        setOwner(player.getUniqueID());
    }

    public boolean isWaterlogged() {
        if(!this.world.getBlockState(this.getPos()).hasProperty(BlockStateProperties.WATERLOGGED)) {
            return false;
        }

        return this.world.getBlockState(this.getPos()).get(BlockStateProperties.WATERLOGGED);
    }
}
