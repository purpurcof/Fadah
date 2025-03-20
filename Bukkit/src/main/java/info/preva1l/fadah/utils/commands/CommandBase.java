package info.preva1l.fadah.utils.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 20/03/2025
 *
 * @author Preva1l
 */
public interface CommandBase {
    void execute(@NotNull CommandArguments command);

    default List<String> onTabComplete(CommandArguments command) {
        return new ArrayList<>();
    }

    default List<String> getDefaultTabComplete(CommandArguments command) {
        List<String> completors = new ArrayList<>();

        List<String> values = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList();

        String[] args = command.args();

        if (args.length == 0) return new ArrayList<>();

        if (!args[args.length - 1].equalsIgnoreCase("")) {
            values.forEach(value -> {
                if (value.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    completors.add(value);
                }
            });
        } else {
            completors.addAll(values);
        }
        return completors;
    }
}
