package info.preva1l.fadah.currency;

import org.bukkit.OfflinePlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * <br><br>
 * Created on 11/03/2025
 *
 * @since 3.0.0
 * @author Preva1l
 * @param id the sub currency id.
 * @param name the sub currency display name.
 * @param requiredPlugin the plugin that is required for the currency to load.
 * @param withdraw turns {@link Currency#withdraw(OfflinePlayer, double)} into a lambda expression.
 * @param add turns {@link Currency#add(OfflinePlayer, double)} into a lambda expression.
 * @param get turns {@link Currency#getBalance(OfflinePlayer)} into a lambda expression.
 * @param preloadCheck a predicate to check if the currency can load or not.
 */
public record SubCurrency(
        String id,
        String name,
        char symbol,
        String requiredPlugin,
        BiConsumer<OfflinePlayer, Double> withdraw,
        BiConsumer<OfflinePlayer, Double> add,
        Function<OfflinePlayer, Double> get,
        Predicate<Void> preloadCheck
) implements Currency {
    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public char getSymbol() {
        return symbol;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequiredPlugin() {
        return requiredPlugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void withdraw(OfflinePlayer player, double amountToTake) {
        withdraw.accept(player, amountToTake);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(OfflinePlayer player, double amountToAdd) {
        add.accept(player, amountToAdd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getBalance(OfflinePlayer player) {
        return get.apply(player);
    }
}
