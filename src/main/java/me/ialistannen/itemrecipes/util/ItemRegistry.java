package me.ialistannen.itemrecipes.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.perceivedev.perceivecore.gui.util.Dimension;

import me.ialistannen.itemrecipes.nodes.ItemRecipeNode;

/**
 * A Registry, that maps {@link ItemStack}s to {@link ItemRecipeNode}s
 */
public enum ItemRegistry {
    INSTANCE;

    private Map<ItemStack, ItemRecipeNode> nodeMap = new HashMap<>();

    /**
     * @param recipeNode The {@link ItemRecipeNode} to add
     */
    public void addRecipeNode(ItemRecipeNode recipeNode) {
        nodeMap.put(recipeNode.getResultNormalized(), recipeNode);
    }

    /**
     * @param itemStack The {@link ItemStack} you want the node for
     *
     * @return The {@link ItemRecipeNode}, if any
     */
    public ItemRecipeNode getNode(ItemStack itemStack) {
        return nodeMap.get(itemStack);
    }

    /**
     * Returns all nodes for the given Category
     *
     * @param itemCategory The {@link ItemCategory} of the nodes
     *
     * @return All nodes with that category
     */
    public List<ItemRecipeNode> getNodes(ItemCategory itemCategory) {
        List<ItemRecipeNode> nodes = new LinkedList<>();
        for (Entry<ItemStack, ItemRecipeNode> entry : nodeMap.entrySet()) {
            ItemCategory nodeCategory = ItemCategory.fromMaterial(entry.getKey().getType());
            if (nodeCategory == itemCategory) {
                nodes.add(entry.getValue());
            }
        }

        return nodes;
    }

    /**
     * Builds the Item registry
     */
    public void build(Dimension dimension) {
        long start = System.currentTimeMillis();
        for (Recipe recipe : RecipeRegistry.INSTANCE.getAllRecipes()) {
            ItemStack result = Util.normalize(recipe.getResult());
            ItemRecipeNode itemRecipeNode = new ItemRecipeNode(null, recipe, dimension);
            nodeMap.put(result, itemRecipeNode);
        }
        long duration = System.currentTimeMillis() - start;
        System.out.println("ItemRegistry.build() Took: " + duration + " (" + TimeUnit.MILLISECONDS.toSeconds(duration) + ")");
    }
}
