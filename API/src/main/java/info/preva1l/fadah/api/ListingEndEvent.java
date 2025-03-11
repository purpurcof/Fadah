package info.preva1l.fadah.api;

import info.preva1l.fadah.records.listing.Listing;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * The listing end event is called when a listing ends but was not purchased.
 * <p>
 * To listen for listing purchases use {@link ListingPurchaseEvent}
 * <br><br>
 * Created on 27/06/2024
 *
 * @author Preva1l
 * @see ListingPurchaseEvent
 */
@SuppressWarnings({"LombokGetterMayBeUsed"})
public final class ListingEndEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final ListingEndReason reason;
    private final Listing listing;

    /**
     * Create a new instance of this event.
     *
     * @param listing the listing.
     * @param reason the reason the listing was ended.
     */
    public ListingEndEvent(Listing listing, ListingEndReason reason) {
        super();
        this.listing = listing;
        this.reason = reason;
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
     * Get the reason the listing was ended.
     *
     * @return the listing end reason.
     */
    public ListingEndReason getReason() {
        return reason;
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
