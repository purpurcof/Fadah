package info.preva1l.fadah.processor;

import org.bukkit.inventory.ItemStack;

/**
 * Processor argument types modify how the string is built to parse into the JavaScriptProcessor.
 * <br><br>
 * Created on 20/02/2025
 *
 * @author Preva1l
 */
public enum ProcessorArgType {
    /**
     * Wraps the result of {@link ProcessorArg#parse(ItemStack)} in {@code ""}.
     */
    STRING,
    /**
     * Does not modify the result of {@link ProcessorArg#parse(ItemStack)}.
     */
    INTEGER,
}
