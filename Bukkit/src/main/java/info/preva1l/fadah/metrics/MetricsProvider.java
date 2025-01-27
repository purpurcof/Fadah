package info.preva1l.fadah.metrics;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.cache.ListingCache;
import info.preva1l.fadah.config.Config;

public interface MetricsProvider {
    int METRICS_ID = 21651;

    void setMetrics(Metrics metrics);

    Metrics getMetrics();

    default void setupMetrics(Fadah plugin) {
        Fadah.getConsole().info("Starting Metrics...");

        setMetrics(new Metrics(plugin, METRICS_ID));
        getMetrics().addCustomChart(new Metrics.SingleLineChart("items_listed", () -> ListingCache.getListings().size()));
        getMetrics().addCustomChart(new Metrics.SimplePie("database_type", () -> Config.i().getDatabase().getType().getFriendlyName()));
        getMetrics().addCustomChart(new Metrics.SimplePie(
                "multi_server",
                () -> Config.i().getBroker().isEnabled() ? Config.i().getBroker().getType().getDisplayName() : "None"
                ));

        Fadah.getConsole().info("Metrics Logging Started!");
    }

    default void shutDownMetrics() {
        if (getMetrics() != null) {
            getMetrics().shutdown();
            Fadah.getConsole().info("Metrics Logging Stopped!");
        }
    }
}
