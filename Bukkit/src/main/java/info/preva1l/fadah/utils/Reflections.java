package info.preva1l.fadah.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.lang.reflect.Method;

/**
 * Created on 29/04/2025
 *
 * @author Preva1l
 */
public class Reflections {
    public static InventoryHolder getHolder(Player player) {
        try {
            Method getOpenInventory = Player.class.getMethod("getOpenInventory");
            getOpenInventory.setAccessible(true);
            Object inventoryView = getOpenInventory.invoke(player);

            Method getTopInventory = inventoryView.getClass().getMethod("getTopInventory");
            getTopInventory.setAccessible(true);
            Inventory topInventory = (Inventory) getTopInventory.invoke(inventoryView);

            return topInventory.getHolder(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
