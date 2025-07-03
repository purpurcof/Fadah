package info.preva1l.fadah.currency;

import info.preva1l.fadah.config.CurrencySettings;
import info.preva1l.fadah.config.misc.SubEconomy;
import lombok.Getter;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CoinsEngineCurrency implements MultiCurrency {
    private final String id = "coins_engine";
    private final String requiredPlugin = "CoinsEngine";
    private final List<Currency> currencies = new ArrayList<>();

    @Override
    public boolean isEnabled() {
        return CurrencySettings.i().getVault().isEnabled();
    }

    @Override
    public boolean preloadChecks() {
        for (SubEconomy eco : CurrencySettings.i().getCoinsEngine().getCurrencies()) {
            Currency subCur = new SubCurrency(
                    id + "_" + eco.economy(),
                    eco.displayName(),
                    eco.symbol(),
                    requiredPlugin,
                    (p, a) -> CoinsEngineAPI.removeBalance(p.getUniqueId(), getCurrency(eco), a),
                    (p, a) -> CoinsEngineAPI.addBalance(p.getUniqueId(), getCurrency(eco), a),
                    p -> CoinsEngineAPI.getBalance(p.getUniqueId(), getCurrency(eco)),
                    v -> {
                        if (getCurrency(eco) == null) {
                            CurrencyService.instance.logger.severe("-------------------------------------");
                            CurrencyService.instance.logger.severe("Cannot enable coins engine currency!");
                            CurrencyService.instance.logger.severe("No currency with name: " + eco.economy());
                            CurrencyService.instance.logger.severe("-------------------------------------");
                            return false;
                        }
                        return true;
                    }
            );
            currencies.add(subCur);
        }
        return true;
    }

    private su.nightexpress.coinsengine.api.currency.Currency getCurrency(SubEconomy eco) {
        return CoinsEngineAPI.getCurrency(eco.economy());
    }
}
