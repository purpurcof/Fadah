package info.preva1l.fadah.processor;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * A registry of {@link ProcessorArg}.
 * <br><br>
 * Created on 20/02/2025
 *
 * @author Preva1l
 */
public final class ProcessorArgsRegistry {
    private static final List<ProcessorArg> values = new ArrayList<>();

    /**
     * Registries can not be initialized.
     */
    private ProcessorArgsRegistry() {
        throw new UnsupportedOperationException("Registries can not be initialized.");
    }

    /**
     * Build and register a {@link ProcessorArg}.
     *
     * @param argType the argument type for the replacer.
     * @param placeholder the string to replace, excluding the surrounding {@code %%}.
     * @param parser the parser function.
     * @see ProcessorArg
     */
    public static void register(@NotNull ProcessorArgType argType, @NotNull String placeholder, Function<@NotNull ItemStack, @NotNull String> parser) {
        Preconditions.checkNotNull(parser);
        Logger.getLogger("Fadah").info("[Services] [JSProcessorService] Matcher Registered: " + placeholder);
        values.add(
                new ProcessorArg(
                        argType,
                        "%" + placeholder + "%",
                        parser
                )
        );
    }

    /**
     * Get all the currently registered {@link ProcessorArg}'s.
     *
     * @return an immutable list of all registered replacers.
     */
    public static List<ProcessorArg> get() {
        return Collections.unmodifiableList(values);
    }
}