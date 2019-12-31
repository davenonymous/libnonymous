package com.davenonymous.libnonymous.render;

import com.davenonymous.libnonymous.utils.BlockStateSerializationHelper;
import com.davenonymous.libnonymous.utils.FloodFill;
import com.davenonymous.libnonymous.utils.Logz;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiblockBlockModel {
    public Map<BlockPos, BlockState> blocks;
    public ResourceLocation id;

    public int width = 0;
    public int height = 0;
    public int depth = 0;

    public MultiblockBlockModel(ResourceLocation id) {
        this.id = id;
    }

    public void setBlocks(Map<BlockPos, BlockState> blocks) {
        this.blocks = blocks;
        width = 0;
        height = 0;
        depth = 0;
        for(BlockPos pos : blocks.keySet()) {
            if(pos.getX() > width)width = pos.getX();
            if(pos.getY() > height)height = pos.getY();
            if(pos.getZ() > depth)depth = pos.getZ();
        }
    }

    public void setBlocksByFloodFill(IWorldReader world, BlockPos pos) {
        FloodFill floodFill = new FloodFill(world, pos);
        Map<BlockPos, BlockState> connectedBlocks = floodFill.getConnectedBlocks();
        if(connectedBlocks.size() == 0) {
            return;
        }

        this.setBlocks(connectedBlocks);
    }

    public int getBlockCount() {
        return this.blocks.keySet().size();
    }

    public Set<BlockPos> getRelevantPositions() {
        return this.blocks.keySet();
    }

    public double getScaleRatio(boolean inclHeight) {
        int dim = Math.max(width, depth);
        if(inclHeight || height > 10) {
            dim = Math.max(height, dim);
        }

        dim += 1;
        if(height > 6 || dim <= 4) {
            dim = Math.max(6, dim);
        }

        return 1.0d / (double)dim;
    }

    public String serializePretty() {
        if(width == 0 || height == 0 || depth == 0) {
            Logz.warn("Can not serialize model for type: '%s', invalid dimensions: w=%d, h=%d, d=%d", id, width, height, depth);
            return "";
        }

        char[][][] map = new char[width+1][height+1][depth+1];
        StringBuilder refMapBuilder = new StringBuilder();
        refMapBuilder.append("  \"ref\": {\n");
        char nextRef = 'a';
        Map<String, Character> refMap = new HashMap<>();
        for(Map.Entry<BlockPos, BlockState> entry : this.blocks.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();

            JsonObject json = BlockStateSerializationHelper.serializeBlockState(state);
            String jsonString = json.toString();

            // Get new or already used reference char for this block
            char thisRef;
            if(refMap.containsKey(jsonString)) {
                thisRef = refMap.get(jsonString);
            } else {
                thisRef = nextRef++;
                refMap.put(jsonString, thisRef);

                refMapBuilder.append("    \""+thisRef+"\": " + jsonString + ",\n");
            }

            map[pos.getX()][pos.getY()][pos.getZ()] = thisRef;
        }
        refMapBuilder.deleteCharAt(refMapBuilder.length()-2);
        refMapBuilder.append("  },\n");

        StringBuilder output = new StringBuilder("{\n");

        output.append("  \"type\": \"" + this.id.toString() + "\",\n");
        output.append("  \"version\": 3,\n");
        output.append(refMapBuilder);

        output.append("  \"shape\": [\n");

        for(int x = map.length-1; x >= 0; x--) {
            output.append("    [\n");
            for(int y = map[x].length-1; y >= 0; y--) {
                StringBuilder builder = new StringBuilder();
                for(int z = 0; z < map[x][y].length; z++) {
                    char chr = ' ';
                    if(map[x][y][z] != '\u0000') {
                        chr = map[x][y][z];
                    }
                    builder.append(chr);
                }

                output.append("      \"" + builder + "\",\n");
            }
            output.deleteCharAt(output.length()-2);
            output.append("    ],\n");
        }
        output.deleteCharAt(output.length()-2);

        output.append("  ]\n}\n");

        return output.toString();
    }
}
