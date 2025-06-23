package info.preva1l.fadah.filters;

import org.bukkit.inventory.ItemStack;

/**
 * Matcher argument types modify how the string is parsed in the MatcherService.
 * <br><br>
 * Created on 20/02/2025
 *
 * @author Preva1l
 */
public enum MatcherArgType {
    /**
     * Wraps the result of {@link MatcherArg#parse(ItemStack)} in {@code ""}.
     */
    STRING,
    /**
     * Does not modify the result of {@link MatcherArg#parse(ItemStack)}.
     */
    INTEGER,
}
