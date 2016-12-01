package me.ialistannen.itemrecipes.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.perceivedev.perceivecore.gui.base.AbstractPane;
import com.perceivedev.perceivecore.gui.base.Pane;
import com.perceivedev.perceivecore.gui.components.Button;
import com.perceivedev.perceivecore.gui.components.panes.tree.TreePane;
import com.perceivedev.perceivecore.gui.components.panes.tree.TreePaneNode;
import com.perceivedev.perceivecore.gui.util.Dimension;
import com.perceivedev.perceivecore.util.ItemFactory;

import me.ialistannen.itemrecipes.util.ItemRegistry;
import me.ialistannen.itemrecipes.util.Util;

/**
 * A {@link TreePaneNode}, that displays the recipe for an item
 */
public class ItemRecipeNode extends TreePaneNode {

    private Pane      pane;
    private ItemStack result;
    private Recipe    recipe;
    private Dimension size;

    /**
     * Creates a new {@link TreePaneNode} with the given parent and no children
     *
     * @param parent The parent node
     * @param recipe The {@link Recipe}
     * @param size The size of the pane
     */
    public ItemRecipeNode(TreePaneNode parent, Recipe recipe, Dimension size) {
        super(parent);

        this.result = recipe.getResult();
        this.recipe = recipe;
        this.size = size;
    }

    /**
     * @return The resulting item
     */
    public ItemStack getResult() {
        return result.clone();
    }

    /**
     * @return The item, with a count of one and normalized damage value
     */
    public ItemStack getResultNormalized() {
        return Util.normalize(result);
    }

    /**
     * @param parent The new parent
     */
    public void setParent(TreePaneNode parent) {
        super.setParent(parent);
    }

    /**
     * Returns the Pane to display for that node
     *
     * @return The pane for the node
     */
    @Override
    public Pane getPane() {
        if (pane == null) {
            pane = new RecipePane(size.getWidth(), size.getHeight());
        }
        return pane;
    }

    private class RecipePane extends AbstractPane {

        private List<List<ItemStack>> items;

        /**
         * An empty Pane
         *
         * @param width The width of this pane
         * @param height The height of this pane
         */
        private RecipePane(int width, int height) {
            super(width, height);

            if (recipe instanceof ShapedRecipe) {
                items = shapedRecipeToList((ShapedRecipe) recipe);
            } else if (recipe instanceof ShapelessRecipe) {
                items = shapelessRecipeToList((ShapelessRecipe) recipe);
            } else {
                items = new ArrayList<>();
            }
        }

        /**
         * @param shapelessRecipe The {@link ShapelessRecipe} to convert
         *
         * @return The converted {@link ShapedRecipe}
         */
        private List<List<ItemStack>> shapelessRecipeToList(ShapelessRecipe shapelessRecipe) {
            List<List<ItemStack>> items = new ArrayList<>();

            List<ItemStack> tmp = new ArrayList<>();
            for (ItemStack itemStack : shapelessRecipe.getIngredientList()) {
                if (tmp.size() == 3) {
                    items.add(tmp);
                    tmp = new ArrayList<>();
                }
                tmp.add(itemStack);
            }
            if (!tmp.isEmpty()) {
                items.add(tmp);
            }

            return items;
        }

        /**
         * @param shapedRecipe The {@link ShapedRecipe} to convert
         *
         * @return The converted {@link ShapedRecipe}
         */
        private List<List<ItemStack>> shapedRecipeToList(ShapedRecipe shapedRecipe) {
            List<List<ItemStack>> items = new ArrayList<>();

            List<ItemStack> tmp = new ArrayList<>();
            for (String s : shapedRecipe.getShape()) {
                for (char c : s.toCharArray()) {
                    tmp.add(shapedRecipe.getIngredientMap().get(c));
                }
                items.add(tmp);
                tmp = new ArrayList<>();
            }
            items.add(tmp);

            return items;
        }

        @Override
        public void render(Inventory inventory, Player player, int x, int y) {
            // clear it
            new ArrayList<>(components).forEach(this::removeComponent);

            for (int yPos = 0; yPos < items.size(); yPos++) {
                List<ItemStack> row = items.get(yPos);

                for (int xPos = 0; xPos < row.size(); xPos++) {
                    ItemStack itemStack = row.get(xPos);

                    if (itemStack == null) {
                        continue;
                    }

                    Optional<TreePane> owner = getOwner();
                    if (!owner.isPresent()) {
                        continue;
                    }

                    TreePane treePane = owner.get();

                    RecipeButton recipeButton = new RecipeButton(Util.normalize(itemStack), Dimension.ONE, treePane);
                    if (getInventoryMap().addComponent(xPos + 3, yPos, recipeButton)) {
                        components.add(recipeButton);
                        updateComponentHierarchy(recipeButton);
                    }
                }
            }

            if (getParent() != null) {
                int xPos = getWidth() / 2;
                int yPos = getHeight() - 1;

                ItemStack parentItem;

                if (getParent() instanceof ItemRecipeNode) {
                    parentItem = ((ItemRecipeNode) getParent()).getResult();
                } else {
                    parentItem = ItemFactory.builder(Material.INK_SACK).setDurability((short) 1).build();
                }

                Button backButton = new Button(parentItem, Dimension.ONE);
                backButton.setAction(clickEvent -> getOwner().ifPresent(treePane -> treePane.select(getParent())));

                if (getInventoryMap().addComponent(xPos, yPos, backButton)) {
                    components.add(backButton);
                    updateComponentHierarchy(backButton);
                }
            }

            super.render(inventory, player, x, y);
        }
    }

    /**
     * A button displaying a recipe
     */
    static class RecipeButton extends Button {

        /**
         * Constructs a button
         *
         * @param itemStack The ItemStack to display
         * @param size The size of the button
         * @param pane The pane this button is in
         *
         * @throws NullPointerException if any parameter is null
         */
        RecipeButton(ItemStack itemStack, Dimension size, TreePane pane) {
            super(itemStack, size);

            setAction(clickEvent -> {
                ItemRecipeNode node = ItemRegistry.INSTANCE.getNode(itemStack);
                if (node == null || (!(node.recipe instanceof ShapedRecipe) && !(node.recipe instanceof ShapelessRecipe))) {
                    return;
                }

                node.setOwner(pane);
                pane.getSelected().ifPresent(node::setParent);

                pane.select(node);
            });
        }

    }
}
