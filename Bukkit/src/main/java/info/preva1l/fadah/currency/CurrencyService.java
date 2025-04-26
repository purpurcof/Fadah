package info.preva1l.fadah.currency;

import info.preva1l.fadah.Fadah;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;

import java.util.stream.Stream;

@Service
public class CurrencyService {
    public static final CurrencyService instance = new CurrencyService();

    @Configure
    public void loadCurrencies() {
        Fadah.getConsole().info("Loading currencies...");
        Stream.of(
                new VaultCurrency(),
                new PlayerPointsCurrency(),
                new RedisEconomyCurrency(),
                new CoinsEngineCurrency()
        ).forEach(CurrencyRegistry::register);
        Fadah.getConsole().info("Currencies Loaded!");
    }
}
