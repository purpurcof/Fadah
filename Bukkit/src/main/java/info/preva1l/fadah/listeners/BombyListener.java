package info.preva1l.fadah.listeners;

import info.preva1l.fadah.commands.subcommands.SellSubCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * listener because bomby is a B.A.N. (dont think about what that means for too long, thanks)
 * <p>
 * Created on 15/06/2025
 *
 * @author Preva1l
 */
public class BombyListener implements Listener {
    @EventHandler
    public void on(BlockPlaceEvent event) {
        if (SellSubCommand.running.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        if (SellSubCommand.running.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerSwapHandItemsEvent event) {
        if (SellSubCommand.running.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
