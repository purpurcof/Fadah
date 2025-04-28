package info.preva1l.fadah.currency;

import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import info.preva1l.trashcan.flavor.annotations.inject.Inject;

import java.util.logging.Logger;
import java.util.stream.Stream;

@Service
public final class CurrencyService {
    public static final CurrencyService instance = new CurrencyService();

    @Inject public Logger logger;

    @Configure
    public void loadCurrencies() {
        Stream.of(
                new VaultCurrency(),
                new PlayerPointsCurrency(),
                new RedisEconomyCurrency(),
                new CoinsEngineCurrency()
        ).forEach(CurrencyRegistry::register);
    }
}
