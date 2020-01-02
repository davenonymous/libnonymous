package com.davenonymous.libnonymous.utils;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Map;

public class RecipeHelper {
    private static final Field recipesField = ObfuscationReflectionHelper.findField(RecipeManager.class, "field_199522_d");

    public static boolean registerRecipe(RecipeManager manager, IRecipeType<?> type, IRecipe recipe) {
        Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipeTypeMap = null;
        try {
            recipesField.setAccessible(true);
            recipeTypeMap = (Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>>) recipesField.get(manager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(recipeTypeMap == null) {
            return false;
        }

        Map<ResourceLocation, IRecipe<?>> recipes = recipeTypeMap.get(type);
        if(recipes == null) {
            return false;
        }

        recipes.put(recipe.getId(), recipe);
        return true;
    }

    public static boolean removeRecipe(RecipeManager manager, IRecipeType<?> type, String recipeId) {
        Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipeTypeMap = null;
        try {
            recipesField.setAccessible(true);
            recipeTypeMap = (Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>>) recipesField.get(manager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(recipeTypeMap == null) {
            return false;
        }

        Map<ResourceLocation, IRecipe<?>> recipes = recipeTypeMap.get(type);
        if(recipes == null) {
            return false;
        }

        recipes.remove(ResourceLocation.tryCreate(recipeId));
        return true;
    }
}
