package info.preva1l.fadah.filters;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * An argument for the MatcherService.
 * <p>
 * A MatcherArg is a replacer for the MatcherService that handles category assignment and item restrictions.
 * It enables you to add a hook to prevent a custom item from being listed.
 * <br><br>
 * You do not make an instance of this class,
 * it is made for you when using {@link MatcherArgsRegistry#register(MatcherArgType, String, Function)}
 * <br><br>
 * Created on 20/02/2025
 *
 * @author Preva1l
 * @param type the matcher type
 * @param placeholder the arg to get replaced
 * @param parser a lambda function that parses the ItemStack and returns the expected result
 */
@ApiStatus.Internal
public record MatcherArg(
        MatcherArgType type,
        String placeholder,
        Function<ItemStack, String> parser
) {
    /**
     * Parses the MatcherArg on the provided item.
     *
     * @param item item to parse on.
     * @return result of the replacer.
     */
    @ApiStatus.Internal
    public String parse(ItemStack item) {
        return parser.apply(item);
    }

    /**
     * The arg to get replaced.
     *
     * @return the placeholder.
     */
    @Override
    public String placeholder() {
        return placeholder;
    }
}
