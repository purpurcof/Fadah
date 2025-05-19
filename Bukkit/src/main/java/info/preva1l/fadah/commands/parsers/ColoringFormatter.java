package info.preva1l.fadah.commands.parsers;

import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.config.misc.Tuple;
import info.preva1l.fadah.utils.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.minecraft.extras.caption.ComponentCaptionFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 19/05/2025
 *
 * @author Preva1l
 */
public final class ColoringFormatter implements ComponentCaptionFormatter<CommandSender> {
    @Override
    public @NonNull Component formatCaption(
            final @NonNull Caption captionKey,
            final @NonNull CommandSender recipient,
            final @NonNull String defaultCaption,
            final @NonNull List<@NonNull CaptionVariable> variables
    ) {
        final Map<String, String> replacements = new HashMap<>();
        for (final CaptionVariable variable : variables) {
            replacements.put("%" + variable.key() + "%", variable.value());
        }

        String caption = switch (captionKey.key()) {
            case "exception.invalid_syntax" -> Lang.i().getErrors().getInvalidUsage();
            case "exception.invalid_argument" -> Lang.i().getErrors().getInvalidArgument();
            case "exception.no_permission" -> Lang.i().getErrors().getNoPermission();
            case "exception.no_such_command" -> Lang.i().getErrors().getCommandNotFound();
            case "exception.invalid_sender" -> Lang.i().getErrors().getMustBePlayer();
            default -> defaultCaption;
        };

        return Text.text(
                caption,
                replacements.entrySet().stream().map(e -> Tuple.of(e.getKey(), e.getValue())).toArray(Tuple[]::new)
        );
    }
}