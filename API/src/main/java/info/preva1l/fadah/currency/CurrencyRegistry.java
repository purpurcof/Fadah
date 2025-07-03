package info.preva1l.fadah.currency;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * A thread-safe registry of {@link Currency}.
 * <br><br>
 * Created on 22/10/2024
 *
 * @author Preva1l
 */
public final class CurrencyRegistry {
    private static final Logger LOGGER = Logger.getLogger("Fadah");

    private static final Map<String, Currency> currencies = new ConcurrentHashMap<>();
    private static final List<String> orderedIds = Collections.synchronizedList(new ArrayList<>());

    /**
     * Registries cannot be instantiated.
     */
    private CurrencyRegistry() {
        throw new UnsupportedOperationException("Registries cannot be instantiated.");
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
    public static void registerMulti(@NotNull MultiCurrency currency) {
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
    public static void register(@NotNull CurrencyBase currencyBase) {
        Objects.requireNonNull(currencyBase, "Currency base cannot be null");

        String currencyId = currencyBase.getId().toLowerCase();

        if (currencies.containsKey(currencyId)) {
            throw new IllegalArgumentException("Currency with the id '" + currencyId + "' is already registered!");
        }

        if (!currencyBase.getRequiredPlugin().isEmpty()) {
            Plugin requiredPlugin = Bukkit.getPluginManager().getPlugin(currencyBase.getRequiredPlugin());
            if (requiredPlugin == null || !requiredPlugin.isEnabled()) {
                if (currencyBase.isEnabled())
                    LOGGER.warning("[Services] [CurrencyService] Required plugin '" + currencyBase.getRequiredPlugin() + "' not found or disabled for currency: " + currencyId);
                return;
            }
        }

        if (!currencyBase.preloadChecks()) {
            LOGGER.severe("[Services] [CurrencyService] Preload checks failed for currency: " + currencyId);
            return;
        }

        if (currencyBase instanceof MultiCurrency multiCurrency) {
            registerMultiCurrencyInternal(multiCurrency);
            return;
        }

        if (currencyBase instanceof Currency currency) {
            registerSingleCurrency(currency);
        } else {
            LOGGER.warning("[Services] [CurrencyService] Unknown CurrencyBase implementation: " +
                    currencyBase.getClass().getSimpleName());
        }
    }

    /**
     * Internal method to handle MultiCurrency registration.
     */
    private static void registerMultiCurrencyInternal(@NotNull MultiCurrency multiCurrency) {
        List<Currency> subCurrencies = multiCurrency.getCurrencies();
        if (subCurrencies == null || subCurrencies.isEmpty()) {
            LOGGER.warning("[Services] [CurrencyService] MultiCurrency '" +
                    multiCurrency.getId() + "' has no sub-currencies");
            return;
        }

        for (Currency currency : subCurrencies) {
            if (currency != null) {
                registerSingleCurrency(currency);
            }
        }
    }

    /**
     * Internal method to register a single currency.
     */
    private static void registerSingleCurrency(@NotNull Currency currency) {
        String currencyId = currency.getId().toLowerCase();

        currencies.put(currencyId, currency);
        orderedIds.add(currencyId);

        LOGGER.info("[Services] [CurrencyService] Registered: " + currency.getId());
    }

    /**
     * Get a currency by its id.
     *
     * @param currencyId the currency to get.
     * @return the currency with the specified id, or null if it isn't loaded or enabled.
     */
    public static @Nullable Currency get(@NotNull String currencyId) {
        Objects.requireNonNull(currencyId, "Currency ID cannot be null");

        Currency currency = currencies.get(currencyId.toLowerCase());
        return (currency != null && currency.isEnabled()) ? currency : null;
    }

    /**
     * Unregister a currency.
     *
     * @param currency the currency to unregister.
     */
    public static void unregister(@NotNull Currency currency) {
        Objects.requireNonNull(currency, "Currency cannot be null");

        String currencyId = currency.getId().toLowerCase();

        Currency removed = currencies.remove(currencyId);
        if (removed != null) {
            orderedIds.remove(currencyId);
            LOGGER.info("[Services] [CurrencyService] Unregistered: " + currency.getId());
        }
    }

    /**
     * Get the next enabled currency based on registration order.
     *
     * @param current the current currency
     * @return the next enabled currency, or null if none found
     */
    public static @Nullable Currency getNext(@NotNull Currency current) {
        return getAdjacent(current, 1);
    }

    /**
     * Get the previous enabled currency based on registration order.
     *
     * @param current the current currency
     * @return the previous enabled currency, or null if none found
     */
    public static @Nullable Currency getPrevious(@NotNull Currency current) {
        return getAdjacent(current, -1);
    }

    /**
     * Core logic to get adjacent currency by direction.
     *
     * @param current the current currency
     * @param direction +1 for next, -1 for previous
     * @return the adjacent enabled currency or null
     */
    private static @Nullable Currency getAdjacent(@NotNull Currency current, int direction) {
        Objects.requireNonNull(current, "Current currency cannot be null");

        String currentId = current.getId().toLowerCase();
        List<String> snapshot = new ArrayList<>(orderedIds);
        int currentIndex = snapshot.indexOf(currentId);

        if (currentIndex == -1) return null;

        int size = snapshot.size();

        for (int i = 1; i < size; i++) {
            int nextIndex = currentIndex + (direction * i);

            if (nextIndex < 0 || nextIndex >= size) break;

            String nextId = snapshot.get(nextIndex);
            Currency nextCurrency = currencies.get(nextId);

            if (nextCurrency != null && nextCurrency.isEnabled()) return nextCurrency;
        }

        return null;
    }

    /**
     * Get all currently loaded and enabled currencies in registration order.
     *
     * @return an immutable list of enabled currencies.
     */
    public static @NotNull List<Currency> getAll() {
        return orderedIds.stream()
                .map(currencies::get)
                .filter(Objects::nonNull)
                .filter(Currency::isEnabled)
                .toList();
    }
}