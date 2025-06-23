package info.preva1l.fadah.currency;

import org.bukkit.OfflinePlayer;

/**
 * A currency hook that can be used to make transactions.
 * <br><br>
 * Created on 22/10/2024
 *
 * @author Preva1l
 */
public interface Currency extends CurrencyBase {
    /**
     * The name of the currency.
     *
     * @return the currency's display name.
     */
    String getName();

    /**
     * The symbol for the currency. (Example: $)
     *
     * @return the currencies display symbol.
     * @since 3.2.0
     */
    char getSymbol();

    /**
     * Withdraw money from a player's balance on this currency.
     *
     * @param player the player to take from.
     * @param amountToTake the amount to take.
     * @return true if successful, false if there was a problem
     * @since 3.2.0
     */
    boolean withdraw(OfflinePlayer player, double amountToTake);

    /**
     * Add money to a player's balance on this currency.
     *
     * @param player the player to give money to.
     * @param amountToAdd the amount to give.
     * @return true if successful, false if there was a problem
     * @since 3.2.0
     */
    boolean add(OfflinePlayer player, double amountToAdd);

    /**
     * Get a player's balance.
     *
     * @param player the player whose balance will be retrieved.
     * @return the player's balance.
     */
    double getBalance(OfflinePlayer player);

    /**
     * Check if a player can afford an amount.
     *
     * @param player the player to check.
     * @param amount the amount that needs to be afforded.
     * @return true if the player can afford the amount, else false
     */
    default boolean canAfford(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean preloadChecks() {
        return true;
    }
}
