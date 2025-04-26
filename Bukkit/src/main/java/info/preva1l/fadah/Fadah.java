package info.preva1l.fadah;

import info.preva1l.fadah.api.AuctionHouseAPI;
import info.preva1l.fadah.api.BukkitAuctionHouseAPI;
import info.preva1l.fadah.listeners.PlayerListener;
import info.preva1l.fadah.multiserver.Broker;
import info.preva1l.fadah.utils.Text;
import info.preva1l.fadah.utils.guis.FastInvManager;
import info.preva1l.trashcan.plugin.BasePlugin;
import info.preva1l.trashcan.plugin.annotations.PluginEnable;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.logging.Logger;

public final class Fadah extends BasePlugin {
    @Getter public static Fadah instance;
    @Getter private static Logger console;

    public Fadah() {
        instance = this;
        console = instance.getLogger();
    }

    @PluginEnable
    public void enable() {
        getConsole().info("Enabling the API...");
        AuctionHouseAPI.setInstance(new BukkitAuctionHouseAPI());
        getConsole().info("API Enabled!");

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        FastInvManager.register(this);

        Broker.getInstance().load();

        Text.list(List.of(
                        "&2&l-------------------------------",
                        "&a Finally a Decent Auction House",
                        "&a   has successfully started!",
                        "&2&l-------------------------------")
        ).forEach(Bukkit.getConsoleSender()::sendMessage);
    }
}
