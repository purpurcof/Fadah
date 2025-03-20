package info.preva1l.fadah.commands.subcommands;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.guis.ViewListingsMenu;
import info.preva1l.fadah.utils.commands.CommandArguments;
import info.preva1l.fadah.utils.commands.SubCommand;
import info.preva1l.fadah.utils.commands.SubCommandArgs;
import info.preva1l.fadah.utils.guis.LayoutManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class ActiveListingsSubCommand extends SubCommand {
    public ActiveListingsSubCommand(Fadah plugin) {
        super(plugin, Lang.i().getCommands().getActiveListings().getAliases(), Lang.i().getCommands().getActiveListings().getDescription());
    }

    @SubCommandArgs(name = "active-listings", permission = "fadah.active-listings")
    public void execute(@NotNull CommandArguments command) {
        assert command.getPlayer() != null;
        if (!Config.i().isEnabled()) {
            command.reply(Lang.i().getPrefix() + Lang.i().getErrors().getDisabled());
            return;
        }

        OfflinePlayer owner = command.getPlayer();

        if (command.args().length >= 1 && command.sender().hasPermission("fadah.manage.active-listings")) {
            owner = Bukkit.getOfflinePlayerIfCached(command.args()[0]);
            if (owner == null) {
                command.reply(Lang.i().getPrefix() + Lang.i().getErrors().getPlayerNotFound()
                        .replace("%player%", command.args()[0]));
                return;
            }
        }

        new ViewListingsMenu(
                command.getPlayer(),
                owner,
                LayoutManager.MenuType.ACTIVE_LISTINGS,
                null,
                null,
                null
        ).open(command.getPlayer());
    }
}