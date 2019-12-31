package com.davenonymous.libnonymous.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.IProperty;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class BlockStateSerializationHelper {

    public static void serializeBlockState(PacketBuffer buffer, BlockState state) {
        buffer.writeResourceLocation(state.getBlock().getRegistryName());

        final Collection<IProperty<?>> properties = state.getProperties();
        buffer.writeInt(properties.size());

        for (final IProperty property : properties) {
            buffer.writeString(property.getName());
            buffer.writeString(state.get(property).toString());
        }
    }

    public static BlockState deserializeBlockState(PacketBuffer buffer) {
        final ResourceLocation id = buffer.readResourceLocation();
        final Block block = ForgeRegistries.BLOCKS.getValue(id);

        if (block != null) {
            final int size = buffer.readInt();

            BlockState state = block.getDefaultState();
            for (int i = 0; i < size; i++) {
                final String propName = buffer.readString();
                final String value = buffer.readString();

                // Check the block for the property. Keys = property names.
                final IProperty blockProperty = block.getStateContainer().getProperty(propName);

                if (blockProperty != null) {
                    // Attempt to parse the value with the the property.
                    final Optional<Comparable> propValue = blockProperty.parseValue(value);

                    if (propValue.isPresent()) {
                        // Update the state with the new property.
                        try {
                            state = state.with(blockProperty, propValue.get());
                        } catch (final Exception e) {
                            Logz.error("Failed to read state for block {}. The mod that adds this block has issues.", block.getRegistryName());
                        }
                    }
                }
            }

            return state;
        }

        return Blocks.AIR.getDefaultState();
    }

    public static JsonObject serializeBlockState (BlockState state) {
        JsonObject result = new JsonObject();

        final Block block = state.getBlock();
        result.addProperty("block", block.getRegistryName().toString());
        if(state.getProperties().size() > 0) {
            JsonObject propertiesObj = new JsonObject();

            for (final IProperty property : state.getProperties()) {
                propertiesObj.addProperty(property.getName(), state.get(property).toString());
            }

            result.add("properties", propertiesObj);
        }

        return result;
    }

    public static BlockState deserializeBlockState (JsonObject json) {

        // Read the block from the forge registry.
        final Block block = MCJsonUtils.getBlock(json, "block");

        // Start off with the default state.
        BlockState state = block.getDefaultState();

        // If the properties member exists, attempt to assign properties to the block state.
        if (json.has("properties")) {

            final JsonElement propertiesElement = json.get("properties");

            if (propertiesElement.isJsonObject()) {

                final JsonObject props = propertiesElement.getAsJsonObject();

                // Iterate each member of the properties object. Expecting a simple key to
                // primitive string structure.
                for (final Map.Entry<String, JsonElement> property : props.entrySet()) {

                    // Check the block for the property. Keys = property names.
                    final IProperty blockProperty = block.getStateContainer().getProperty(property.getKey());

                    if (blockProperty != null) {

                        if (property.getValue().isJsonPrimitive()) {

                            // Attempt to parse the value with the the property.
                            final String valueString = property.getValue().getAsString();
                            final Optional<Comparable> propValue = blockProperty.parseValue(valueString);

                            if (propValue.isPresent()) {

                                // Update the state with the new property.
                                try {

                                    state = state.with(blockProperty, propValue.get());
                                }

                                catch (final Exception e) {
                                    Logz.error("Failed to update state for block {}. The mod that adds this block has issues.", block.getRegistryName());
                                }
                            }

                            else {

                                throw new JsonSyntaxException("The property " + property.getKey() + " with value " + valueString + " coul not be parsed!");
                            }
                        }

                        else {

                            throw new JsonSyntaxException("Expected property value for " + property.getKey() + " to be primitive string. Got " + JSONUtils.toString(property.getValue()));
                        }
                    }

                    else {

                        throw new JsonSyntaxException("The property " + property.getKey() + " is not valid for block " + block.getRegistryName());
                    }
                }
            }

            else {

                throw new JsonSyntaxException("Expected properties to be an object. Got " + JSONUtils.toString(propertiesElement));
            }
        }

        return state;
    }
}
