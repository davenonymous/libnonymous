package com.davenonymous.libnonymous.render;

import com.davenonymous.libnonymous.reflections.BlockColorsReflection;
import com.davenonymous.libnonymous.serialization.MultiblockBlockModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Map;

public class MultiBlockBlockColors extends BlockColors {
	private MultiblockBlockModel model;
	private Map<Reference<Block>, BlockColor> shadowBlockColors;

	public MultiBlockBlockColors(MultiblockBlockModel multiBlock) {
		super();
		this.model = multiBlock;
	}

	@Override
	public int getColor(BlockState pState, @Nullable BlockAndTintGetter pLevel, @Nullable BlockPos pPos, int pTintIndex) {
		this.shadowBlockColors = BlockColorsReflection.getBlockColorsMaps(Minecraft.getInstance().getBlockColors());

		var state = model.blocks.getOrDefault(pPos, Blocks.AIR.defaultBlockState());
		BlockColor blockcolor = this.shadowBlockColors.get(ForgeRegistries.BLOCKS.getDelegateOrThrow(state.getBlock()));
		return blockcolor == null ? -1 : blockcolor.getColor(state, pLevel, pPos, pTintIndex);
	}
}