package info.preva1l.fadah.records.listing;

import com.google.common.base.Preconditions;
import info.preva1l.fadah.currency.Currency;
import info.preva1l.fadah.currency.CurrencyRegistry;
import info.preva1l.fadah.records.post.Post;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A listing builder allows you to create a new listing via the API.
 * <br><br>
 * Created on 7/03/2025
 *
 * @author Preva1l
 * @since 3.0.0
 */
public abstract class ListingBuilder {
    /**
     * The listing owners {@link UUID}.
     */
    protected final UUID ownerUuid;
    /**
     * The listing owners name.
     */
    protected final String ownerName;
    /**
     * The item to be sold.
     */
    protected ItemStack itemStack;
    /**
     * The currency to use.
     */
    protected Currency currency = CurrencyRegistry.getAll().getFirst();
    /**
     * The price or the starting bid of the listing.
     */
    protected Double price;
    /**
     * The amount the player will be taxed on sale.
     */
    protected Double tax = 0.0;
    /**
     * How long the listing will last until it expires.
     * <p>
     * This value is in milliseconds and is not a timestamp.
     */
    protected Long length = 2L * 60 * 60 * 1000; // 2 Hour Default
    /**
     * Whether the listing is biddable or buy it now.
     */
    protected boolean biddable = false;

    /**
     * Create a new builder instance.
     *
     * @param owner the player who will own the listing.
     * @since 3.0.0
     */
    public ListingBuilder(Player owner) {
        this.ownerUuid = owner.getUniqueId();
        this.ownerName = owner.getName();
    }

    /**
     * Create a new builder instance.
     *
     * @param ownerUuid the uuid of the player who will own the listing.
     * @param ownerName the name of the player who will own the listing.
     * @since 3.0.0
     */
    public ListingBuilder(UUID ownerUuid, String ownerName) {
        this.ownerUuid = ownerUuid;
        this.ownerName = ownerName;
    }

    /**
     * Set the item that will get listed.
     *
     * @param itemStack the item to list.
     * @return the listing builder instance.
     * @since 3.0.0
     */
    public ListingBuilder itemStack(@NotNull ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack);
        this.itemStack = itemStack;
        return this;
    }

    /**
     * Set the currency that will be used.
     *
     * @param currency the currency to use.
     * @return the listing builder instance.
     * @since 3.0.0
     */
    public ListingBuilder currency(@NotNull Currency currency) {
        Preconditions.checkNotNull(currency);
        this.currency = currency;
        return this;
    }

    /**
     * Set the price OR the starting bid of the listing.
     *
     * @param price the price to set.
     * @return the listing builder instance.
     * @since 3.0.0
     */
    public ListingBuilder price(double price) {
        this.price = price;
        return this;
    }

    /**
     * Set the amount the player will be taxed when the listing is complete.
     *
     * @param tax the percentage of tax to take.
     * @return the listing builder instance.
     * @since 3.0.0
     */
    public ListingBuilder tax(double tax) {
        this.tax = tax;
        return this;
    }

    /**
     * Set how long the listing will last before expiring.
     *
     * @param length length in milliseconds.
     * @return the listing builder instance.
     * @since 3.0.0
     */
    public ListingBuilder length(long length) {
        this.length = length;
        return this;
    }

    /**
     * Set if listing will be biddable or buy it now.
     *
     * @param biddable whether the listing is biddable or not
     * @return the listing builder instance.
     * @since 3.0.0
     */
    public ListingBuilder biddable(boolean biddable) {
        this.biddable = biddable;
        return this;
    }

    /**
     * Convert the listing builder into a post.
     *
     * @return the post.
     * @since 3.0.0
     */
    public abstract Post toPost();

    /**
     * Builds into a listing object.
     * <p>
     * Scans for the correct category and builds the object.
     *
     * @return a completable future of an unlisted listing object
     * @since 3.0.0
     */
    @ApiStatus.Internal
    public abstract CompletableFuture<Listing> build();
}