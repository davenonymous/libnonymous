package com.davenonymous.libnonymous.render;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import javax.annotation.Nullable;

public class MultiBlockModelWorldReader implements IEnviromentBlockReader {
    private MultiblockBlockModel model;

    private IEnviromentBlockReader blockWorld;
    private BlockPos blockPos;

    public MultiBlockModelWorldReader(MultiblockBlockModel model) {
        this.model = model;
    }

    public MultiBlockModelWorldReader(MultiblockBlockModel model, IEnviromentBlockReader blockWorld, BlockPos blockPos) {
        this.model = model;
        this.blockWorld = blockWorld;
        this.blockPos = blockPos;
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return blockWorld == null ? Biomes.FOREST : blockWorld.getBiome(blockPos);
    }

    @Override
    public int getLightFor(LightType type, BlockPos pos) {
        return blockWorld == null ? type.defaultLightValue : blockWorld.getLightFor(type, blockPos);
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if(model.blocks.get(pos) != null) {
            return model.blocks.get(pos);
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public IFluidState getFluidState(BlockPos pos) {
        // TODO: ???
        return null;
    }

    @Override
    public boolean isSkyLightMax(BlockPos pos) {
        return blockWorld == null || blockWorld.isSkyLightMax(blockPos);
    }

    @Override
    public int getCombinedLight(BlockPos pos, int minLight) {
        return blockWorld == null ? 255 : blockWorld.getCombinedLight(blockPos, minLight);
    }
}
