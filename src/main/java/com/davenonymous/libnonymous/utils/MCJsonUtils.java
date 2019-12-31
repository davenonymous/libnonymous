package com.davenonymous.libnonymous.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/*
This class is basically taken as is from
https://github.com/Darkhax-Minecraft/Bookshelf/blob/4ecef76081/src/main/java/net/darkhax/bookshelf/util/MCJsonUtils.java

Thanks @Darkhax
 */
public final class MCJsonUtils {

    public static <T extends IForgeRegistryEntry<T>> T getRegistryEntry (JsonObject json, String memberName, IForgeRegistry<T> registry) {

        if (json.has(memberName)) {

            return getRegistryEntry(json.get(memberName), memberName, registry);
        }

        else {

            throw new JsonSyntaxException("Missing required value " + memberName);
        }
    }

    public static <T extends IForgeRegistryEntry<T>> T getRegistryEntry (JsonElement json, String memberName, IForgeRegistry<T> registry) {

        if (json == null) {

            throw new JsonSyntaxException("The property " + memberName + " is missing.");
        }

        if (json.isJsonPrimitive()) {

            final String rawId = json.getAsString();
            final ResourceLocation registryId = ResourceLocation.tryCreate(rawId);

            if (registryId != null) {

                final T registryEntry = registry.getValue(registryId);

                if (registryEntry != null) {

                    return registryEntry;
                }

                else {

                    throw new JsonSyntaxException("No entry found for id " + rawId);
                }
            }

            else {

                throw new JsonSyntaxException("Registry id " + rawId + " for property " + memberName + " was not a valid format.");
            }
        }

        else {

            throw new JsonSyntaxException("Expected " + memberName + " to be a JSON primitive. was " + JSONUtils.toString(json));
        }
    }

    public static Block getBlock (JsonObject json, String memberName) {

        return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.BLOCKS);
    }

    public static Fluid getFluid (JsonObject json, String memberName) {

        return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.FLUIDS);
    }

    public static Item getItem (JsonObject json, String memberName) {

        return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.ITEMS);
    }

    public static Effect getPotion (JsonObject json, String memberName) {

        return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.POTIONS);
    }

    public static Biome getBiome (JsonObject json, String memberName) {

        return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.BIOMES);
    }

    public static SoundEvent getSound (JsonObject json, String memberName) {

        return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.SOUND_EVENTS);
    }

    public static Potion getPotionType (JsonObject json, String memberName) {

        return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.POTION_TYPES);
    }

    public static Enchantment getEnchantment (JsonObject json, String memberName) {

        return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.ENCHANTMENTS);
    }

    public static EntityType<?> getEntity (JsonObject json, String memberName) {

        return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.ENTITIES);
    }

    public static TileEntityType<?> getTileEntity (JsonObject json, String memberName) {

        return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.TILE_ENTITIES);
    }

    public static ParticleType<?> getParticleType (JsonObject json, String memberName) {

        return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.PARTICLE_TYPES);
    }

    public static PaintingType getPainting (JsonObject json, String memberName) {

        return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.PAINTING_TYPES);
    }

    public static ModDimension getDimension (JsonObject json, String memberName) {

        return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.MOD_DIMENSIONS);
    }

}