package me.ialistannen.itemrecipes.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitRunnable;

import com.perceivedev.perceivecore.gui.util.Dimension;

import me.ialistannen.itemrecipes.ItemRecipes;

/**
 * A registry for Recipes
 */
public enum RecipeRegistry {
    INSTANCE;

    private final Map<ItemStack, Recipe> recipeMap = new ConcurrentHashMap<>();

    /**
     * @param recipe The {@link Recipe} to add
     */
    public void addRecipe(Recipe recipe) {
        recipeMap.put(Util.normalize(recipe.getResult()), recipe);
    }

    /**
     * @param result The result of the recipe
     *
     * @return The Recipe or null if none
     */
    public Recipe getRecipe(ItemStack result) {
        return recipeMap.get(Util.normalize(result));
    }

    /**
     * @return All recipes. Unmodifiable
     */
    public Collection<Recipe> getAllRecipes() {
        return Collections.unmodifiableCollection(recipeMap.values());
    }

    /**
     * Loads the recipes
     */
    public void loadRecipes() {
        init();
    }

    private void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();

                Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
                while (recipeIterator.hasNext()) {
                    Recipe recipe = recipeIterator.next();
                    addRecipe(recipe);
                }
                long duration = System.currentTimeMillis() - start;
                System.out.println("RecipeRegistry.run() Took: " + duration + " (" + TimeUnit.MILLISECONDS.toSeconds(duration) + ")");

                ItemRegistry.INSTANCE.build(new Dimension(9, 5));
            }
        }.runTaskAsynchronously(ItemRecipes.getInstance());
    }

}
