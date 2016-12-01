package me.ialistannen.itemrecipes.nodes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.gui.base.Pane;
import com.perceivedev.perceivecore.gui.components.Button;
import com.perceivedev.perceivecore.gui.components.panes.PagedPane;
import com.perceivedev.perceivecore.gui.components.panes.tree.TreePaneNode;
import com.perceivedev.perceivecore.gui.util.Dimension;
import com.perceivedev.perceivecore.util.ItemFactory;
import com.perceivedev.perceivecore.util.TextUtils;

import me.ialistannen.itemrecipes.util.ItemCategory;

/**
 * The tree pane root node
 */
public class ItemRootNode extends TreePaneNode {

    private List<ItemCategoryNode> nodes = new ArrayList<>();
    private PagedPane pagedPane;
    private Dimension size;

    /**
     * Creates a new {@link TreePaneNode} with the given parent and no children
     *
     * @param parent The parent node
     */
    public ItemRootNode(TreePaneNode parent, Dimension size, ItemCategory... categories) {
        super(parent);

        this.size = size;

        for (ItemCategory category : categories) {
            ItemCategoryNode itemCategoryNode = new ItemCategoryNode(this, category, size);
            nodes.add(itemCategoryNode);
        }
    }

    private PagedPane createPane() {
        PagedPane pagedPane = new PagedPane(size.getWidth(), size.getHeight());

        for (ItemCategoryNode node : nodes) {
            getOwner().ifPresent(node::setOwner);

            ItemStack icon = ItemFactory.builder(node.getCategory().getIcon())
                      .setName("&c&l" + TextUtils.enumFormat(node.getCategory().name(), true))
                      .build();
            Button button = new Button(icon, Dimension.ONE);
            button.setAction(clickEvent -> getOwner().ifPresent(treePane -> treePane.select(node)));

            pagedPane.addComponent(button);

            node.setParent(this);
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
            pagedPane = createPane();
        }
        return pagedPane;
    }
}
