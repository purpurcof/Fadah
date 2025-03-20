package info.preva1l.fadah.utils.commands;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.utils.TaskManager;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Getter
public abstract class SubCommand implements CommandBase {
    private final SubCommandArgs assigned;
    public Fadah plugin;
    private CommandArguments executeArguments;
    private boolean senderHasPermission = false;
    private final List<String> aliases;
    private final String description;

    public SubCommand(Fadah plugin, List<String> aliases, String description) {
        this.plugin = plugin;
        this.description = description;
        this.aliases = aliases;
        this.assigned = Arrays.stream(this.getClass().getMethods()).filter(method -> method.getAnnotation(SubCommandArgs.class) != null).map(method -> method.getAnnotation(SubCommandArgs.class)).findFirst().orElse(null);
    }

    public final void executor(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (this.assigned.inGameOnly() && sender instanceof ConsoleCommandSender) {
            Lang.sendMessage(sender, Lang.i().getPrefix() + Lang.i().getErrors().getMustBePlayer());
            return;
        }
        if (this.assigned.permission() != null && !sender.hasPermission(this.assigned.permission())) {
            senderHasPermission = false;
            Lang.sendMessage(sender, Lang.i().getPrefix() + Lang.i().getErrors().getNoPermission());
            return;
        }

        if (args.length == 0) {
            this.executeArguments = new CommandArguments(sender, label, new String[0]);
        } else {
            this.executeArguments = new CommandArguments(sender, label, Arrays.copyOfRange(args, 1, args.length));
        }

        if (this.assigned.async()) {
            TaskManager.Async.run(plugin, () -> this.execute(executeArguments));
        } else {
            if (sender instanceof Player p) {
                TaskManager.Sync.run(plugin, p, () -> this.execute(executeArguments));
            } else {
                TaskManager.Sync.run(plugin, () -> this.execute(executeArguments));
            }
        }
    }
}
