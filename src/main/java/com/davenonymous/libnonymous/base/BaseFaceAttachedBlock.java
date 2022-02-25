package com.davenonymous.libnonymous.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;

import javax.annotation.Nullable;

public abstract class BaseFaceAttachedBlock extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock {
	public BaseFaceAttachedBlock(Properties properties) {
		super(properties);
	}

	public static Direction getAttachedFace(BlockState pState) {
		return switch(pState.getValue(FACE)) {
			case CEILING -> Direction.DOWN;
			case FLOOR -> Direction.UP;
			default -> pState.getValue(FACING);
		};
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if(!level.isClientSide()) {
			return (lvl, pos, stt, te) -> {
				if(te instanceof BaseBlockEntity blockEntity) {
					blockEntity.tickServer();
				}
			};
		} else {
			return (lvl, pos, stt, te) -> {
				if(te instanceof BaseBlockEntity blockEntity) {
					blockEntity.tickClient();
				}
			};
		}
	}


	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);

		if(!(world.getBlockEntity(pos) instanceof BaseBlockEntity<?>)) {
			return;
		}

		var baseTile = (BaseBlockEntity) world.getBlockEntity(pos);
		baseTile.loadFromItem(stack);
	}

	public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity, boolean horizontalOnly) {
		var result = Direction.getNearest((float) (entity.getX() - clickedBlock.getX()), (float) (entity.getY() - clickedBlock.getY()), (float) (entity.getZ() - clickedBlock.getZ()));
		if(horizontalOnly && (result == Direction.UP || result == Direction.DOWN)) {
			return Direction.NORTH;
		}
		return result;
	}
}