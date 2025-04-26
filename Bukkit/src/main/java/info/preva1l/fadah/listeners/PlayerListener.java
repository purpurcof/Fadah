package info.preva1l.fadah.listeners;

import com.github.puregero.multilib.regionized.RegionizedTask;
import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.data.DataService;
import info.preva1l.fadah.data.DatabaseManager;
import info.preva1l.fadah.guis.NewListingMenu;
import info.preva1l.fadah.utils.TaskManager;
import info.preva1l.fadah.utils.Text;
import info.preva1l.fadah.utils.UpdateService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final Map<UUID, RegionizedTask> invalidateIfNoJoin = new HashMap<>();

    @EventHandler
    public void joinListener(AsyncPlayerPreLoginEvent e) {
        if (!DatabaseManager.getInstance().isConnected()) {
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            e.kickMessage(Text.text(Lang.i().getPrefix() + Lang.i().getErrors().getDatabaseLoading()));
            return;
        }

        invalidateIfNoJoin.put(e.getUniqueId(), TaskManager.Sync.runLater(Fadah.getInstance(),
                () -> DataService.instance
                        .invalidateAndSavePlayerData(e.getUniqueId())
                        .thenRun(() -> invalidateIfNoJoin.remove(e.getUniqueId())), 1200L));
        DataService.instance.loadPlayerData(e.getUniqueId()).join();
    }

    @EventHandler
    public void finalJoin(PlayerJoinEvent e) {
        RegionizedTask task = invalidateIfNoJoin.remove(e.getPlayer().getUniqueId());
        if (task != null) {
            task.cancel();
        }
        UpdateService.instance.notifyUpdate(e.getPlayer());
    }

    @EventHandler
    public void leaveListener(PlayerQuitEvent e) {
        DataService.instance.invalidateAndSavePlayerData(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void pickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.getOpenInventory().getTopInventory().getHolder(false) instanceof NewListingMenu) {
            event.setCancelled(true);
        }
    }
}
