package info.preva1l.fadah.currency;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * A registry of {@link Currency}.
 * <br><br>
 * Created on 22/10/2024
 *
 * @author Preva1l
 */
public final class CurrencyRegistry {
    private static final Map<Integer, String> enumerator = new ConcurrentHashMap<>();
    private static final Map<String, Currency> values = new ConcurrentHashMap<>();

    /**
     * Registries can not be initialized.
     */
    private CurrencyRegistry() {
        throw new UnsupportedOperationException("Registries can not be initialized.");
    }

    /**
     * Register a multi-currency hook.
     * <p>
     * This registers all the {@link MultiCurrency} sub-currencies as their own {@link Currency} entry.
     *
     * @deprecated use {@link CurrencyRegistry#register(CurrencyBase)} instead.
     * @param currency the currency to register.
     * @throws IllegalArgumentException when a currency is already registered with one of the provided sub currency ids.
     * @see CurrencyRegistry#register(CurrencyBase)
     */
    @Deprecated(since = "3.0.0")
    public static void registerMulti(MultiCurrency currency) {
        register(currency);
    }

    /**
     * Register a currency hook.
     * <p>
     * If a {@link MultiCurrency} is loaded it will load all of its sub-currencies as their own {@link Currency} entry.
     *
     * @param currencyBase the currency to register.
     * @throws IllegalArgumentException when a currency is already registered with one of the provided ids.
     * @since 3.0.0
     * @see CurrencyRegistry#registerMulti(MultiCurrency)
     */
    public static void register(CurrencyBase currencyBase) {
        if (values.containsKey(currencyBase.getId())) {
            throw new IllegalArgumentException("Currency with the id " + currencyBase.getId() + " is already registered!");
        }

        if (!currencyBase.getRequiredPlugin().isEmpty()) {
            Plugin requiredPlugin = Bukkit.getPluginManager().getPlugin(currencyBase.getRequiredPlugin());
            if (requiredPlugin == null || !requiredPlugin.isEnabled()) return;
        }

        if (!currencyBase.preloadChecks()) {
            Logger.getLogger("Fadah")
                    .severe("[CurrencyService] Tried enabling %s but the preload checks failed!"
                            .formatted(currencyBase.getId().toLowerCase()));
            return;
        }

        if (currencyBase instanceof MultiCurrency multiCurrency) {
            for (Currency currency : multiCurrency.getCurrencies()) {
                register(currency);
            }
            return;
        }

        // Sanity check in-case someone implements CurrencyBase for some unknown reason
        if (!(currencyBase instanceof Currency currency)) return;

        values.put(currency.getId().toLowerCase(), currency);
        enumerator.put(enumerator.size(), currency.getId().toLowerCase());

        Logger.getLogger("Fadah").info("[CurrencyService] Registered: " + currency.getId());
    }

    /**
     * Get a currency by its id.
     * <p>
     * The currency id must be lowercase, but this is automatically fixed in this method.
     *
     * @param currencyId the currency to get.
     * @return the currency with the specified id, or null if it isn't loaded.
     */
    public static Currency get(String currencyId) {
        return values.get(currencyId.toLowerCase());
    }

    /**
     * Unregister a currency.
     *
     * @param currency the currency to unregister.
     * @implNote This method is pretty slow but should be fast enough to run on the main thread.
     */
    public static void unregister(@NotNull Currency currency) {
        String currencyId = currency.getId().toLowerCase();

        values.remove(currencyId);

        int removedKey = enumerator.entrySet().stream()
                .filter(entry -> entry.getValue().equals(currencyId))
                .findFirst()
                .map(Map.Entry::getKey).orElse(-1);

        if (removedKey != -1) {
            enumerator.remove(removedKey);
            enumerator.entrySet().removeIf(entry -> entry.getKey() > removedKey);

            int newKey = removedKey;
            for (Map.Entry<Integer, String> entry : enumerator.entrySet()) {
                enumerator.put(newKey++, entry.getValue());
            }
        }
    }

    /**
     * Get the currency after the provided currency based of the enumerator order.
     *
     * @param current the seed.
     * @return the next currency.
     */
    public static Currency getNext(@NotNull Currency current) {
        Integer currentIndex = null;
        for (Map.Entry<Integer, String> entry : enumerator.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(current.getId())) {
                currentIndex = entry.getKey();
                break;
            }
        }
        if (currentIndex == null || currentIndex == enumerator.size() - 1) {
            return null;
        }
        int nextIndex = currentIndex + 1;
        String nextCurrencyId = enumerator.get(nextIndex);
        return values.get(nextCurrencyId);
    }

    /**
     * Get the currency before the provided currency based of the enumerator order.
     *
     * @param current the seed.
     * @return the previous currency.
     */
    public static Currency getPrevious(@NotNull Currency current) {
        Integer currentIndex = null;
        for (Map.Entry<Integer, String> entry : enumerator.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(current.getId())) {
                currentIndex = entry.getKey();
                break;
            }
        }
        if (currentIndex == null || currentIndex == 0) {
            return null;
        }
        int previousIndex = currentIndex - 1;
        String previousCurrencyId = enumerator.get(previousIndex);
        return values.get(previousCurrencyId);
    }

    /**
     * Get all the currently loaded currencies.
     *
     * @return an immutable list.
     */
    public static List<Currency> getAll() {
        return List.copyOf(values.values());
    }
}