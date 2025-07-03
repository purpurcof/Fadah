package info.preva1l.fadah.api;

import info.preva1l.fadah.records.listing.Listing;
import info.preva1l.fadah.records.post.Post;
import info.preva1l.fadah.records.post.PostResult;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The listing create event is called when a new listing is posted to the auction house.
 * <p>
 * This event does not get called if {@code Post#callEvent} is false
 * <br><br>
 * Created on 27/06/2024
 *
 * @author Preva1l
 * @see Post
 */
@SuppressWarnings({"LombokGetterMayBeUsed","LombokSetterMayBeUsed"})
public final class ListingCreateEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final @Nullable Player player;
    private final Listing listing;
    private boolean cancelled = false;
    private String cancelReason = "A 3rd Party Hook has cancelled the creation of this listing!";

    /**
     * Create a new instance of this event.
     *
     * @param who the player who crated the listing.
     * @param listing the listing.
     */
    public ListingCreateEvent(@Nullable Player who, @NotNull Listing listing) {
        this.player = who;
        this.listing = listing;
    }

    /**
     * The player who created the listing.
     *
     * @return the player or null if the listing was made through the API and the owner UUID was not a player.
     */
    public @Nullable Player getPlayer() {
        return player;
    }

    /**
     * The listing that has been created.
     *
     * @return the listing object.
     */
    public Listing getListing() {
        return listing;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * If this event is cancelled, set the reason that will be put into a {@link PostResult#custom(String)}
     *
     * @param reason the reason the listing creation was cancelled
     * @see Post
     */
    public void setCancelReason(String reason) {
        this.cancelReason = reason;
    }

    /**
     * If the event is cancelled, get the reason it was cancelled.
     *
     * @return the reason the listing creation was cancelled.
     */
    public String getCancelReason() {
        return cancelReason;
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
