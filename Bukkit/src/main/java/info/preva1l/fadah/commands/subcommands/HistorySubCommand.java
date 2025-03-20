package info.preva1l.fadah.commands.subcommands;

import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.guis.HistoryMenu;
import info.preva1l.fadah.utils.commands.CommandArguments;
import info.preva1l.fadah.utils.commands.SubCommandArgs;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class HistorySubCommand extends BaseProfileSubCommand {
    public HistorySubCommand() {
        super(Lang.i().getCommands().getHistory().getAliases(), Lang.i().getCommands().getHistory().getDescription());
    }

    @SubCommandArgs(name = "history", permission = "fadah.history")
    public void execute(@NotNull CommandArguments command) {
        OfflinePlayer owner = checkEnabledPermissionAndLoaded(command, "fadah.manage.history");
        if (command.getPlayer() == null || owner == null) return;

        new HistoryMenu(command.getPlayer(), owner, null).open(command.getPlayer());
    }
}
