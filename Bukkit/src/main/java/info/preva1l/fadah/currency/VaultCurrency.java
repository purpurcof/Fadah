package info.preva1l.fadah.currency;

import info.preva1l.fadah.config.CurrencySettings;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

@Getter
public class VaultCurrency implements Currency {
    private final String id = "vault";
    private final String requiredPlugin = "Vault";

    private Economy economy;

    @Override
    public String getName() {
        return CurrencySettings.i().getVault().getName();
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
    public boolean withdraw(OfflinePlayer player, double amountToTake) {
        if (economy == null) {
            throw new RuntimeException("Vault has no compatible economy plugin.");
        }
        return economy.withdrawPlayer(player, amountToTake).transactionSuccess();
    }

    @Override
    public boolean add(OfflinePlayer player, double amountToAdd) {
        if (economy == null) {
            throw new RuntimeException("Vault has no compatible economy plugin.");
        }
        return economy.depositPlayer(player, amountToAdd).transactionSuccess();
    }


    @Override
    public double getBalance(OfflinePlayer player) {
        if (economy == null) {
            throw new RuntimeException("Vault has no compatible economy plugin.");
        }
        return economy.getBalance(player);
    }

    @Override
    public boolean preloadChecks() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            CurrencyService.instance.logger.severe("---------------------------------------------------------");
            CurrencyService.instance.logger.severe("Cannot enable vault currency! No Economy Plugin Installed");
            CurrencyService.instance.logger.severe("---------------------------------------------------------");
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }
}
