package info.preva1l.fadah.currency;

import info.preva1l.fadah.config.CurrencySettings;
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
        return CurrencySettings.i().getPlayerPoints().getName();
    }

    @Override
    public char getSymbol() {
        return CurrencySettings.i().getPlayerPoints().getSymbol();
    }

    @Override
    public boolean isEnabled() {
        return CurrencySettings.i().getVault().isEnabled();
    }

    @Override
    public boolean preloadChecks() {
        this.api = PlayerPoints.getInstance().getAPI();
        if (api == null) {
            CurrencyService.instance.logger.severe("-------------------------------------");
            CurrencyService.instance.logger.severe("Cannot enable player points currency!");
            CurrencyService.instance.logger.severe("Plugin did not start correctly.");
            CurrencyService.instance.logger.severe("-------------------------------------");
            return false;
        }
        return true;
    }

    @Override
    public boolean withdraw(OfflinePlayer player, double amountToTake) {
        return this.api.take(player.getUniqueId(), (int) amountToTake);
    }

    @Override
    public boolean add(OfflinePlayer player, double amountToAdd) {
        return this.api.give(player.getUniqueId(), (int) amountToAdd);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return this.api.look(player.getUniqueId());
    }
}
