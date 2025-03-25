package info.preva1l.fadah.commands;

import info.preva1l.fadah.Fadah;

public interface CommandProvider {
    Fadah getPlugin();

    default void loadCommands() {
        Fadah.getConsole().info("Loading commands...");
        new AuctionHouseCommand(getPlugin());
        new MigrateCommand(getPlugin());
        Fadah.getConsole().info("Commands Loaded!");
    }
}
