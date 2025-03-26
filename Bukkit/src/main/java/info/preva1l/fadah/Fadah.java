package info.preva1l.fadah;

import info.preva1l.fadah.api.AuctionHouseAPI;
import info.preva1l.fadah.api.BukkitAuctionHouseAPI;
import info.preva1l.fadah.commands.CommandProvider;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.currency.CurrencyProvider;
import info.preva1l.fadah.data.DataProvider;
import info.preva1l.fadah.data.DatabaseManager;
import info.preva1l.fadah.hooks.HookProvider;
import info.preva1l.fadah.listeners.PlayerListener;
import info.preva1l.fadah.metrics.MetricsProvider;
import info.preva1l.fadah.migrator.MigrationProvider;
import info.preva1l.fadah.migrator.MigratorManager;
import info.preva1l.fadah.multiserver.Broker;
import info.preva1l.fadah.processor.DefaultProcessorArgsProvider;
import info.preva1l.fadah.utils.Text;
import info.preva1l.fadah.utils.UpdatesProvider;
import info.preva1l.fadah.utils.config.BasicConfig;
import info.preva1l.fadah.utils.guis.FastInvManager;
import info.preva1l.fadah.utils.guis.LayoutManager;
import info.preva1l.fadah.utils.logging.LoggingProvider;
import info.preva1l.hooker.Hooker;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
import java.util.stream.Stream;

public final class Fadah extends JavaPlugin implements MigrationProvider, CurrencyProvider, CommandProvider,
        MetricsProvider, LoggingProvider, HookProvider, DataProvider, DefaultProcessorArgsProvider, UpdatesProvider {
    @Getter private static Fadah instance;
    @Getter private static Logger console;

    @Getter private final Logger transactionLogger = Logger.getLogger("AuctionHouse-Transactions");
    @Getter private BasicConfig categoriesFile;
    @Getter private BasicConfig menusFile;

    @Getter private LayoutManager layoutManager;

    @Getter @Setter private MigratorManager migrationManager;

    @Override
    public void onLoad() {
        instance = this;
        console = getLogger();
        loadHooks();
    }

    @Override
    public void onEnable() {
        getConsole().info("Enabling the API...");
        AuctionHouseAPI.setInstance(new BukkitAuctionHouseAPI());
        getConsole().info("API Enabled!");

        registerDefaultProcessorArgs();
        loadCurrencies();
        loadMenus();
        loadFiles();
        loadDataAndPopulateCaches();
        loadCommands();

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        FastInvManager.register(this);

        Broker.getInstance().load();

        Hooker.enable();
        loadMigrators();

        initLogger();
        setupMetrics();

        Bukkit.getConsoleSender().sendMessage(Text.modernMessage("""
                        &2&l------------------------------
                        &a Finally a Decent Auction House
                        &a   has successfully started!
                        &2&l------------------------------""".trim()));

        checkForUpdates();
    }

    @Override
    public void onDisable() {
        FastInvManager.closeAll(this);
        DatabaseManager.getInstance().shutdown();
        if (Config.i().getBroker().isEnabled()) Broker.getInstance().destroy();
        shutdownMetrics();
    }

    private void loadFiles() {
        getConsole().info("Loading Configuration Files...");
        categoriesFile = new BasicConfig(this, "categories.yml");

        Config.i();
        Lang.i();

        categoriesFile.save();
        categoriesFile.load();
        getConsole().info("Configuration Files Loaded!");
    }

    private void loadMenus() {
        layoutManager = new LayoutManager();
        Stream.of(
                new BasicConfig(this, "menus/main.yml"),
                new BasicConfig(this, "menus/new-listing.yml"),
                new BasicConfig(this, "menus/expired-listings.yml"),
                new BasicConfig(this, "menus/active-listings.yml"),
                new BasicConfig(this, "menus/historic-items.yml"),
                new BasicConfig(this, "menus/confirm.yml"),
                new BasicConfig(this, "menus/collection-box.yml"),
                new BasicConfig(this, "menus/profile.yml"),
                new BasicConfig(this, "menus/view-listings.yml"),
                new BasicConfig(this, "menus/watch.yml")
        ).forEach(layoutManager::loadLayout);
    }

    @Override
    public Fadah getPlugin() {
        return this;
    }
}
