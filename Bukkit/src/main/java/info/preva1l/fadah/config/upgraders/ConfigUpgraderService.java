package info.preva1l.fadah.config.upgraders;

import info.preva1l.fadah.config.upgraders.impl.CurrencyConfigUpgrader;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import info.preva1l.trashcan.flavor.annotations.inject.Inject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created on 16/06/2025
 *
 * @author Preva1l
 */
@Service(priority = 100)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigUpgraderService {
    public static final ConfigUpgraderService instance = new ConfigUpgraderService();

    @Inject private Logger logger;

    @Configure
    public void configure() {
        Stream.of(
                new CurrencyConfigUpgrader(logger)
        ).forEach(ConfigUpgrader::migrate);
    }
}
