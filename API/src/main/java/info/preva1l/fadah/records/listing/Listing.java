package info.preva1l.fadah.records.listing;

import info.preva1l.fadah.currency.Currency;
import info.preva1l.fadah.currency.CurrencyRegistry;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.SortedSet;
import java.util.UUID;

/**
 * A listing that gets posted to the auction house.
 * <br><br>
 * Created on 13/04/2024
 *
 * @author Preva1l
 */
@Getter
public abstract class Listing {
    final @NotNull UUID id;
    final @NotNull UUID owner;
    final @NotNull String ownerName;
    final @NotNull ItemStack itemStack;
    final @NotNull String categoryID;
    final @NotNull String currencyId;
    final double price;
    final double tax;
    final long creationDate;
    final long deletionDate;
    final SortedSet<Bid> bids;

    /**
     * The constructor for a listing.
     *
     * @param id the listing's id. (should be randomly generated)
     * @param owner the owner of the listing.
     * @param ownerName the listing owners name.
     * @param itemStack the item being sold.
     * @param categoryID the category the listing is in.
     * @param currency the currency the listing was posted with.
     * @param price the buy now price OR the starting bid.
     * @param tax the percentage the listing owner will be taxed on the sale
     * @param creationDate epoch timestamp of when the listing was created.
     * @param deletionDate epoch timestamp of when the listing will expire.
     * @param bids a (sorted) set of all the bids on the listing. sorted by the timestamp.
     */
    protected Listing(@NotNull UUID id, @NotNull UUID owner, @NotNull String ownerName,
                      @NotNull ItemStack itemStack, @NotNull String categoryID, @NotNull String currency, double price,
                      double tax, long creationDate, long deletionDate, SortedSet<Bid> bids) {
        this.id = id;
        this.owner = owner;
        this.ownerName = ownerName;
        this.itemStack = itemStack;
        this.categoryID = categoryID;
        this.currencyId = currency;
        this.price = price;
        this.tax = tax;
        this.creationDate = creationDate;
        this.deletionDate = deletionDate;
        this.bids = bids;
    }

    /**
     * Get the currency the listing was posted with.
     *
     * @return the currency used to list the item.
     */
    public Currency getCurrency() {
        return CurrencyRegistry.get(currencyId);
    }

    /**
     * Check if a player owns the listing.
     *
     * @param player the player to check.
     * @return true if the player is the owner.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isOwner(@NotNull Player player) {
        return player.getUniqueId().equals(this.owner);
    }

    /**
     * Check if a player owns the listing.
     *
     * @param uuid the player's uuid to check.
     * @return true if the player is the owner.
     */
    public boolean isOwner(@NotNull UUID uuid) {
        return this.owner.equals(uuid);
    }

    /**
     * Purchase the listing if it is active.
     *
     * @param buyer the player buying the listing.
     * @throws IllegalStateException if the listing is not a {@code Buy It Now} listing.
     */
    public abstract void purchase(@NotNull Player buyer);

    /**
     * Add a new bid to the listing if it is active.
     *
     * @param bidder the player placing the bid.
     * @param bidAmount the amount of the bid.
     * @return true if the bid was successful, false if the bid is not high enough or the player is already the highest bidder.
     */
    public abstract boolean newBid(@NotNull Player bidder, double bidAmount);

    /**
     * Cancels the listing if it is active.
     *
     * @param canceller the player attempting to cancel the listing.
     * @return true if it was cancelled, false if the player does not own the listing or have management permissions.
     */
    public abstract boolean cancel(@NotNull Player canceller);

    /**
     * Check if a player can buy the listing, or place a bid.
     *
     * @param player the player to check.
     * @return true if the player can purchase the listing, else false.
     */
    public abstract boolean canBuy(@NotNull Player player);
}