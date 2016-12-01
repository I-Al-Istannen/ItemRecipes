package me.ialistannen.itemrecipes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.perceivedev.perceivecore.gui.Gui;
import com.perceivedev.perceivecore.gui.components.panes.tree.TreePane;
import com.perceivedev.perceivecore.util.TextUtils;

import me.ialistannen.itemrecipes.nodes.ItemRootNode;
import me.ialistannen.itemrecipes.util.ItemCategory;
import me.ialistannen.itemrecipes.util.RecipeRegistry;

public final class ItemRecipes extends JavaPlugin {

    private static ItemRecipes instance;

    public void onEnable() {
        instance = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                RecipeRegistry.INSTANCE.loadRecipes();
            }
        }.runTask(this);
    }

    @Override
    public void onDisable() {
        // prevent the old instance from still being around.
        instance = null;
    }

    /**
     * Returns the plugins instance
     *
     * @return The plugin instance
     */
    public static ItemRecipes getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player.");
            return true;
        }

        Player player = (Player) sender;

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            sender.sendMessage(ChatColor.RED + "You have no item in your main hand.");
            return true;
        }

        Material type = itemStack.getType();
        ItemCategory itemCategory = ItemCategory.fromMaterial(type);

        sender.sendMessage(ChatColor.LIGHT_PURPLE + TextUtils.enumFormat(type.name(), true)
                  + ChatColor.DARK_PURPLE + ": "
                  + ChatColor.GREEN + itemCategory.getNmsName());

        TreePane pane = new TreePane(9, 5);
        ItemRootNode node = new ItemRootNode(null, pane.getSize(), ItemCategory.values());
        pane.setRoot(node);

        Gui gui = new Gui("Test", 5, pane);

        gui.open(player);

        return true;
    }
}
