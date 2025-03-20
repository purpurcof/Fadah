package info.preva1l.fadah.commands.subcommands;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.utils.commands.CommandArguments;
import info.preva1l.fadah.utils.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
public abstract class BaseProfileSubCommand extends SubCommand {
    protected BaseProfileSubCommand(List<String> aliases, String description) {
        super(Fadah.getINSTANCE(), aliases, description);
    }

    protected @Nullable OfflinePlayer checkEnabledPermissionAndLoaded(CommandArguments command, String permission) {
        assert command.getPlayer() != null;
        if (!Config.i().isEnabled()) {
            command.reply(Lang.i().getPrefix() + Lang.i().getErrors().getDisabled());
            return null;
        }

        OfflinePlayer owner = command.getPlayer();

        if (command.args().length >= 1 && command.sender().hasPermission(permission)) {
            owner = Bukkit.getOfflinePlayerIfCached(command.args()[0]);
            if (owner == null) {
                command.reply(Lang.i().getPrefix() + Lang.i().getErrors().getPlayerNotFound()
                        .replace("%player%", command.args()[0]));
                return null;
            }
            Fadah.getINSTANCE().loadPlayerData(owner.getUniqueId()).join();
        }

        return owner;
    }
}
