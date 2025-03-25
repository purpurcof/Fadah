package info.preva1l.fadah.metrics;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.cache.CacheAccess;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.records.listing.Listing;

public interface MetricsProvider {
    int METRICS_ID = 21651;

    MetricsHolder metricsHolder = new MetricsHolder();

    default void setupMetrics() {
        getPlugin().getLogger().info("Starting Metrics...");

        metricsHolder.metrics = new Metrics(getPlugin(), METRICS_ID);
        metricsHolder.metrics.addCustomChart(new Metrics.SingleLineChart("items_listed", () -> CacheAccess.getAll(Listing.class).size()));
        metricsHolder.metrics.addCustomChart(new Metrics.SimplePie("database_type", () -> Config.i().getDatabase().getType().getFriendlyName()));
        metricsHolder.metrics.addCustomChart(new Metrics.SimplePie(
                "multi_server",
                () -> Config.i().getBroker().isEnabled() ? Config.i().getBroker().getType().getDisplayName() : "None"
        ));


        getPlugin().getLogger().info("Metrics Logging Started!");
    }

    default void shutdownMetrics() {
        if (metricsHolder.metrics != null) {
            metricsHolder.metrics.shutdown();
        }
    }

    class MetricsHolder {
        private Metrics metrics;
    }

    Fadah getPlugin();
}
