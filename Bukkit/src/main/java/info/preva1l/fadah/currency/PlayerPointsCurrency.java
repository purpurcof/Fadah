package info.preva1l.fadah.currency;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Config;
import lombok.Getter;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.OfflinePlayer;

/**
 * Created on 27/03/2025
 *
 * @author Preva1l
 */
@Getter
public class PlayerPointsCurrency implements Currency {
    private final String id = "player_points";
    private final String requiredPlugin = "PlayerPoints";

    private PlayerPointsAPI api;

    @Override
    public String getName() {
        return Config.i().getCurrency().getPlayerPoints().getName();
    }

    @Override
    public boolean preloadChecks() {
        this.api = PlayerPoints.getInstance().getAPI();
        if (api == null) {
            Fadah.getConsole().severe("-------------------------------------");
            Fadah.getConsole().severe("Cannot enable player points currency!");
            Fadah.getConsole().severe("Plugin did not start correctly.");
            Fadah.getConsole().severe("-------------------------------------");
            return false;
        }
        return true;
    }

    @Override
    public void withdraw(OfflinePlayer player, double amountToTake) {
        this.api.take(player.getUniqueId(), (int) amountToTake);
    }

    @Override
    public void add(OfflinePlayer player, double amountToAdd) {
        this.api.give(player.getUniqueId(), (int) amountToAdd);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return this.api.look(player.getUniqueId());
    }
}
