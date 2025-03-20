package info.preva1l.fadah.commands.subcommands;

import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.guis.CollectionMenu;
import info.preva1l.fadah.utils.commands.CommandArguments;
import info.preva1l.fadah.utils.commands.SubCommandArgs;
import info.preva1l.fadah.utils.guis.LayoutManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class ExpiredItemsSubCommand extends BaseProfileSubCommand {
    public ExpiredItemsSubCommand() {
        super(Lang.i().getCommands().getExpiredItems().getAliases(), Lang.i().getCommands().getExpiredItems().getDescription());
    }

    @SubCommandArgs(name = "expired-items", permission = "fadah.expired-items")
    public void execute(@NotNull CommandArguments command) {
        OfflinePlayer owner = checkEnabledPermissionAndLoaded(command, "fadah.manage.expired-items");
        if (command.getPlayer() == null || owner == null) return;

        new CollectionMenu(command.getPlayer(), owner, LayoutManager.MenuType.EXPIRED_LISTINGS).open(command.getPlayer());
    }
}
