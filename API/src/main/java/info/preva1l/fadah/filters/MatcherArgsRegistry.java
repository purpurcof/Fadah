package info.preva1l.fadah.filters;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * A registry of {@link MatcherArg}.
 * <br><br>
 * Created on 20/02/2025
 *
 * @author Preva1l
 */
public final class MatcherArgsRegistry {
    private static final List<MatcherArg> values = new ArrayList<>();

    /**
     * Registries can not be initialized.
     */
    private MatcherArgsRegistry() {
        throw new UnsupportedOperationException("Registries can not be initialized.");
    }

    /**
     * Build and register a {@link MatcherArg}.
     *
     * @param argType the argument type for the replacer.
     * @param placeholder the string to replace, excluding the surrounding {@code %%}.
     * @param parser the parser function.
     * @see MatcherArg
     */
    public static void register(@NotNull MatcherArgType argType, @NotNull String placeholder, Function<@NotNull ItemStack, @NotNull String> parser) {
        Preconditions.checkNotNull(parser);
        Logger.getLogger("Fadah").info("[Services] [MatcherService] Matcher Registered: " + placeholder);
        values.add(
                new MatcherArg(
                        argType,
                        "%" + placeholder + "%",
                        parser
                )
        );
    }

    /**
     * Get all the currently registered {@link MatcherArg}'s.
     *
     * @return an immutable list of all registered replacers.
     */
    public static List<MatcherArg> get() {
        return Collections.unmodifiableList(values);
    }
}