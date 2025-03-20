package info.preva1l.fadah.commands.subcommands;

import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.guis.CollectionMenu;
import info.preva1l.fadah.utils.commands.CommandArguments;
import info.preva1l.fadah.utils.commands.SubCommandArgs;
import info.preva1l.fadah.utils.guis.LayoutManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class CollectionBoxSubCommand extends BaseProfileSubCommand {
    public CollectionBoxSubCommand() {
        super(Lang.i().getCommands().getCollectionBox().getAliases(), Lang.i().getCommands().getCollectionBox().getDescription());
    }

    @SubCommandArgs(name = "collection-box", permission = "fadah.collection-box")
    public void execute(@NotNull CommandArguments command) {
        OfflinePlayer owner = checkEnabledPermissionAndLoaded(command, "fadah.manage.collection-box");
        if (command.getPlayer() == null || owner == null) return;

        new CollectionMenu(command.getPlayer(), owner, LayoutManager.MenuType.COLLECTION_BOX).open(command.getPlayer());
    }
}
