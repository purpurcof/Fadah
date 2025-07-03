package info.preva1l.fadah.commands.parsers;

import info.preva1l.fadah.config.misc.Tuple;
import info.preva1l.fadah.utils.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.minecraft.extras.caption.ComponentCaptionFormatter;
import org.incendo.cloud.minecraft.extras.caption.RichVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 19/05/2025
 *
 * @author Preva1l
 */
@SuppressWarnings("unchecked")
public final class ColoringFormatter implements ComponentCaptionFormatter<CommandSender> {
    public @NonNull Component formatCaption(final @NonNull Caption captionKey, final @NonNull CommandSender recipient, final @NonNull String caption, final @NonNull List<@NonNull CaptionVariable> variables) {
        final List<Tuple<String, Object>> replacements = new ArrayList<>();

        for (CaptionVariable variable : variables) {
            String key = variable.key();
            if (variable instanceof RichVariable) {
                replacements.add(Tuple.of("%" + key + "%", ((RichVariable) variable).component()));
            } else {
                replacements.add(Tuple.of("%" + key + "%", variable.value()));
            }
        }

        return Text.text(caption, replacements.toArray(Tuple[]::new));
    }
}