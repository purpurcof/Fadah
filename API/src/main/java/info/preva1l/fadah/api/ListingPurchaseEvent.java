package info.preva1l.fadah.api;

import info.preva1l.fadah.records.listing.Listing;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * The listing purchase event is called when a player purchases a listing or has the winning bid.
 * <br><br>
 * Created on 27/06/2024
 *
 * @author Preva1l
 */
@SuppressWarnings({"LombokGetterMayBeUsed"})
public final class ListingPurchaseEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Listing listing;
    private final OfflinePlayer buyer;

    /**
     * Create a new instance of this event.
     *
     * @param listing the listing.
     * @param buyer the player who purchased the listing.
     */
    public ListingPurchaseEvent(Listing listing, OfflinePlayer buyer) {
        super();
        this.listing = listing;
        this.buyer = buyer;
    }

    /**
     * Get the listing.
     * <p>
     * At the point this event is called the listing is in a stale state so it is for data retrieval only,
     * any other methods will not work
     *
     * @return the (stale) listing.
     */
    public Listing getListing() {
        return listing;
    }

    /**
     * Get the player who purchased the listing.
     *
     * @return the buyer.
     */
    public OfflinePlayer getBuyer() {
        return buyer;
    }

    /**
     * Gets the events handler list.
     *
     * @return the handler list.
     */
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * {@inheritDoc}
     */
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
