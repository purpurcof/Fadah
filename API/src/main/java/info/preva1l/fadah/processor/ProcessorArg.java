package info.preva1l.fadah.processor;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * An argument for the JavaScriptProcessor.
 * <p>
 * A ProcessorArg is a replacer for the JavaScriptProcessor that handles category assignment and item restrictions.
 * It enables you to add a hook to prevent a custom item from being listed.
 * <br><br>
 * You do not make an instance of this class,
 * it is made for you when using {@link ProcessorArgsRegistry#register(ProcessorArgType, String, Function)}
 * <br><br>
 * Created on 20/02/2025
 *
 * @author Preva1l
 * @param type the processor type
 * @param placeholder the arg to get replaced
 * @param parser a lambda function that parses the ItemStack and returns the expected result
 */
@ApiStatus.Internal
public record ProcessorArg(
        ProcessorArgType type,
        String placeholder,
        Function<ItemStack, String> parser
) {
    /**
     * Parses the ProcessorArg on the provided item.
     *
     * @param item item to parse on.
     * @return result of the replacer.
     */
    @ApiStatus.Internal
    public String parse(ItemStack item) {
        return parser.apply(item);
    }
}
