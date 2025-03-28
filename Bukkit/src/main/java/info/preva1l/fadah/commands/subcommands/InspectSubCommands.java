package info.preva1l.fadah.commands.subcommands;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.guis.ViewListingsMenu;
import info.preva1l.fadah.utils.commands.CommandArguments;
import info.preva1l.fadah.utils.commands.SubCommand;
import info.preva1l.fadah.utils.commands.SubCommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class InspectSubCommands {
    private static void inspect(CommandArguments command, OfflinePlayer owner) {
        assert command.getPlayer() != null;
        if (owner == null) owner = Bukkit.getOfflinePlayerIfCached(command.args()[0]);

        if (owner == null) {
            command.reply(Lang.i().getPrefix() + Lang.i().getErrors().getPlayerNotFound()
                    .replace("%player%", command.args()[0]));
            return;
        }

        new ViewListingsMenu(
                command.getPlayer(),
                owner,
                null,
                null,
                null
        ).open(command.getPlayer());
    }

    private static boolean enabled(CommandArguments command) {
        if (Config.i().isEnabled()) return true;
        command.reply(Lang.i().getPrefix() + Lang.i().getErrors().getDisabled());
        return false;
    }

    public static class ViewSubCommand extends SubCommand {
        public ViewSubCommand(Fadah plugin) {
            super(plugin, Lang.i().getCommands().getView().getAliases(), Lang.i().getCommands().getView().getDescription());
        }

        @SubCommandArgs(name = "view", permission = "fadah.view")
        public void execute(@NotNull CommandArguments command) {
            if (!enabled(command)) return;

            if (command.args().length == 0) {
                command.reply(Lang.i().getPrefix() + Lang.i().getErrors().getInvalidUsage()
                        .replace("%command%", Lang.i().getCommands().getView().getUsage()));
                return;
            }

            inspect(command, null);
        }
    }

    public static class ActiveListingsSubCommand extends SubCommand {
        public ActiveListingsSubCommand(Fadah plugin) {
            super(plugin, Lang.i().getCommands().getActiveListings().getAliases(), Lang.i().getCommands().getActiveListings().getDescription());
        }

        @SubCommandArgs(name = "active-listings", permission = "fadah.active-listings")
        public void execute(@NotNull CommandArguments command) {
            if (!enabled(command)) return;

            OfflinePlayer owner = null;
            if (command.args().length < 1 || !command.sender().hasPermission("fadah.manage.active-listings")) {
                owner = command.getPlayer();
            }

            inspect(command, owner);
        }
    }
}