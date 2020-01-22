package com.davenonymous.libnonymous.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BaseBlock extends Block {
    public BaseBlock(Properties properties) {
        super(properties);
    }

    public void renderEffectOnHeldItem(PlayerEntity player, Hand mainHand, float partialTicks) {

    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, world, pos, neighbor);

        if(world.isRemote()) {
            return;
        }

        TileEntity tileEntity = world.getTileEntity(pos);
        if(!(tileEntity instanceof BaseTileEntity)) {
            return;
        }

        BaseTileEntity base = (BaseTileEntity) tileEntity;
        int previous = base.getIncomingRedstonePower();
        int now = base.getRedstonePowerFromNeighbors();

        if(now == 0) {
            if(previous > 0) {
                base.redstonePulse();
            }
        } else {
            if(previous != now) {
                base.redstoneChanged(previous, now);
            }
        }

        base.setIncomingRedstonePower(now);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if(!(world.getTileEntity(pos) instanceof BaseTileEntity)) {
            return;
        }

        BaseTileEntity baseTile = (BaseTileEntity) world.getTileEntity(pos);
        baseTile.loadFromItem(stack);
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity, boolean horizontalOnly) {
        Direction result = Direction.getFacingFromVector((float) (entity.posX - clickedBlock.getX()), (float) (entity.posY - clickedBlock.getY()), (float) (entity.posZ - clickedBlock.getZ()));
        if(horizontalOnly && (result == Direction.UP || result == Direction.DOWN)) {
            return Direction.NORTH;
        }
        return result;
    }
}
