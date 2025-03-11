package info.preva1l.fadah.currency;

import java.util.List;

/**
 * A currency hook that has the ability to load multiple currencies from one hook.
 * <br><br>
 * Created on 24/10/2024
 *
 * @author Preva1l
 */
public interface MultiCurrency extends CurrencyBase {
    /**
     * Get all the sub-currencies.
     *
     * @return an immutable list of the sub-currencies.
     */
    List<Currency> getCurrencies();
}
