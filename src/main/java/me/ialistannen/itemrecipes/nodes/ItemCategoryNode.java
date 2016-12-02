package me.ialistannen.itemrecipes.nodes;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.bukkit.Material;

import com.perceivedev.perceivecore.gui.base.Pane;
import com.perceivedev.perceivecore.gui.components.Button;
import com.perceivedev.perceivecore.gui.components.panes.AnchorPane;
import com.perceivedev.perceivecore.gui.components.panes.PagedPane;
import com.perceivedev.perceivecore.gui.components.panes.tree.TreePane;
import com.perceivedev.perceivecore.gui.components.panes.tree.TreePaneNode;
import com.perceivedev.perceivecore.gui.util.Dimension;
import com.perceivedev.perceivecore.util.ItemFactory;

import me.ialistannen.itemrecipes.nodes.ItemRecipeNode.RecipeButton;
import me.ialistannen.itemrecipes.util.ItemCategory;
import me.ialistannen.itemrecipes.util.ItemRegistry;
import me.ialistannen.itemrecipes.util.Util;

/**
 * A {@link TreePaneNode}, that displays all items in an {@link ItemCategory}
 */
class ItemCategoryNode extends TreePaneNode {

    private ItemCategory category;
    private Dimension    size;
    private PagedPane    pagedPane;

    /**
     * Creates a new {@link TreePaneNode} with the given parent and no children
     *
     * @param parent The parent node
     * @param category The {@link ItemCategory}
     * @param size The size of the Pane
     */
    ItemCategoryNode(TreePaneNode parent, ItemCategory category, Dimension size) {
        super(parent);
        this.category = category;
        this.size = size;
    }

    /**
     * @return The {@link ItemCategory}
     */
    ItemCategory getCategory() {
        return category;
    }

    private PagedPane generatePagedPane() {
        PagedPane pagedPane = new PagedPane(size.getWidth(), size.getHeight());

        BiConsumer<PagedPane, AnchorPane> pagePopulateFunction = pagedPane.getPagePopulateFunction();
        pagedPane.setPagePopulateFunction((pagedPane1, anchorPane) -> {
            pagePopulateFunction.accept(pagedPane1, anchorPane);

            int x = pagedPane1.getWidth() / 2 - 1;
            int y = pagedPane1.getHeight() - 1;

            Button backButton = new Button(ItemFactory.builder(Material.BARRIER).setName("&c&lBack").build(), Dimension.ONE);
            backButton.setAction(clickEvent -> getOwner().ifPresent(treePane -> treePane.select(getParent())));

            anchorPane.addComponent(backButton, x, y);
        });

        Optional<TreePane> owner = getOwner();

        if (!owner.isPresent()) {
            return pagedPane;
        }

        TreePane treePane = owner.get();

        List<ItemRecipeNode> nodes = ItemRegistry.INSTANCE.getNodes(category)
                  .stream()
                  .sorted((o1, o2) -> o1.getResult().getType().compareTo(o2.getResult().getType()))
                  .collect(Collectors.toList());
        for (ItemRecipeNode node : nodes) {
            node.setParent(this);

            RecipeButton button = new RecipeButton(Util.normalize(node.getResult()), Dimension.ONE, treePane);
            pagedPane.addComponent(button);
        }

        return pagedPane;
    }

    /**
     * Returns the Pane to display for that node
     *
     * @return The pane for the node
     */
    @Override
    public Pane getPane() {
        if (pagedPane == null) {
            pagedPane = generatePagedPane();
        }
        return pagedPane;
    }
}
