package info.preva1l.fadah.currency;

import org.jetbrains.annotations.ApiStatus;

/**
 * <b>DO NOT EXTEND</b> You should implement either {@link Currency} or {@link MultiCurrency}.
 * <p>
 * The base of {@link Currency} and {@link MultiCurrency} for their common methods.
 * <br><br>
 * Created on 11/03/2025
 *
 * @since 3.0.0
 * @author Preva1l
 */
@ApiStatus.NonExtendable
public interface CurrencyBase {
    /**
     * The id of the currency.
     *
     * @return the currency's id.
     */
    String getId();

    /**
     * The plugin that is required for the currency to load.
     *
     * @return the required plugins name.
     */
    String getRequiredPlugin();

    /**
     * Pre startup checks for the currency hook.
     * <br>
     * The currency will not load if this returns false.
     *
     * @return true if the checks succeed false if they fail.
     */
    boolean preloadChecks();
}
