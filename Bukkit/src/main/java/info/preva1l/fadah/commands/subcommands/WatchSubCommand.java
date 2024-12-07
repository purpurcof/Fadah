package info.preva1l.fadah.commands.subcommands;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.guis.WatchMenu;
import info.preva1l.fadah.utils.commands.SubCommand;
import info.preva1l.fadah.utils.commands.SubCommandArgs;
import info.preva1l.fadah.utils.commands.SubCommandArguments;
import org.jetbrains.annotations.NotNull;

public class WatchSubCommand extends SubCommand {
    public WatchSubCommand(Fadah plugin) {
        super(plugin, Lang.i().getCommands().getWatch().getAliases(), Lang.i().getCommands().getWatch().getDescription());
    }

    @SubCommandArgs(name = "watch", permission = "fadah.watch")
    public void execute(@NotNull SubCommandArguments command) {
        if (!Config.i().isEnabled()) {
            command.reply(Lang.i().getPrefix() + Lang.i().getErrors().getDisabled());
            return;
        }
        assert command.getPlayer() != null;

        new WatchMenu(command.getPlayer()).open(command.getPlayer());
    }
}
