package me.ialistannen.itemrecipes.util;

import org.bukkit.inventory.ItemStack;

import com.perceivedev.perceivecore.util.ItemFactory;

/**
 * Some utility functions
 */
public class Util {

    /**
     * @param item The item to normalize
     *
     * @return The normalized item
     */
    public static ItemStack normalize(ItemStack item) {
        ItemFactory itemFactory = ItemFactory.builder(item).setAmount(1);
        if (item.getDurability() == Short.MAX_VALUE) {
            itemFactory.setDurability((short) 0);
        }
        return itemFactory.build();
    }
}
