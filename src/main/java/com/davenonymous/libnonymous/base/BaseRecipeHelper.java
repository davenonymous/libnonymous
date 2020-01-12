package com.davenonymous.libnonymous.base;

import com.davenonymous.libnonymous.utils.RecipeData;
import com.davenonymous.libnonymous.utils.RecipeHelper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

public class BaseRecipeHelper<T extends RecipeData> {
    IRecipeType<T> recipeType;

    public BaseRecipeHelper(IRecipeType<T> type) {
        this.recipeType = type;
    }

    public T getRecipe(RecipeManager manager, ResourceLocation id) {
        return (T) getRecipes(manager).getOrDefault(id, null);
    }

    public Stream<T> getRecipeStream(RecipeManager manager) {
        return getRecipes(manager).values().stream().map(r -> (T)r);
    }

    public Map<ResourceLocation, IRecipe<?>> getRecipes(RecipeManager manager) {
        return RecipeHelper.getRecipes(manager, recipeType);
    }

    public T getRandomRecipe(RecipeManager manager, Random rand) {
        Map<ResourceLocation, IRecipe<?>> recipes = getRecipes(manager);
        if(recipes.size() == 0) {
            return null;
        }
        Set<ResourceLocation> ids = recipes.keySet();
        ResourceLocation randomId = (ResourceLocation) ids.toArray()[rand.nextInt(ids.size())];
        return (T) recipes.get(randomId);

    }

}
