package info.preva1l.fadah.commands.subcommands;

import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.guis.ProfileMenu;
import info.preva1l.fadah.utils.commands.CommandArguments;
import info.preva1l.fadah.utils.commands.SubCommandArgs;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class ProfileSubCommand extends BaseProfileSubCommand {
    public ProfileSubCommand() {
        super(Lang.i().getCommands().getProfile().getAliases(), Lang.i().getCommands().getProfile().getDescription());
    }

    @SubCommandArgs(name = "profile", permission = "fadah.profile", async = true)
    public void execute(@NotNull CommandArguments command) {
        OfflinePlayer owner = checkEnabledPermissionAndLoaded(command, "fadah.manage.profiles");
        if (command.getPlayer() == null || owner == null) return;
        new ProfileMenu(command.getPlayer(), owner).open(command.getPlayer());
    }
}
