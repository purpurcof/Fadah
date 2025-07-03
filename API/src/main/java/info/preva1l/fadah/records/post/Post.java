package info.preva1l.fadah.records.post;

import info.preva1l.fadah.api.ListingCreateEvent;
import info.preva1l.fadah.records.listing.ListingBuilder;

/**
 * A post is the final step before publishing a listing,
 * it allows you to configure everything about the initial listing.
 * <br>
 * Created on 7/03/2025
 *
 * @since 3.0.0
 * @author Preva1l
 */
@SuppressWarnings("unused")
public abstract class Post {
    /**
     * The listing builder to post.
     */
    protected final ListingBuilder listingBuilder;
    /**
     * If an advert should attempt to be posted or not.
     */
    protected boolean postAdvert = false;
    /**
     * If the advert should post even if they player cant afford it.
     */
    protected boolean bypassAdvertCost = false;
    /**
     * If the players max listings should be ignored.
     */
    protected boolean bypassMaxListings = false;
    /**
     * If the item restriction checks should be skipped.
     */
    protected boolean bypassRestrictedItems = false;
    /**
     * If the player does not need to pay tax.
     */
    protected boolean bypassTax = false;
    /**
     * If the {@link ListingCreateEvent} should be called.
     */
    protected boolean callEvent = true;
    /**
     * If the player should be notified about the listing being posted.
     */
    protected boolean notifyPlayer = true;
    /**
     * If the listing should get logged into history and file logs.
     */
    protected boolean submitLog = true;
    /**
     * If the listing should alert people watching for a listing.
     */
    protected boolean alertWatchers = true;

    /**
     * Create a new post from a listing builder.
     *
     * @param listing the listing builder to post.
     * @since 3.0.0
     */
    protected Post(ListingBuilder listing) {
        this.listingBuilder = listing;
    }

    /**
     * Set whether an advert should attempt to be posted.
     *
     * @param postAdvert whether an advert should attempt to be posted.
     * @return the post.
     * @since 3.0.0
     */
    public Post postAdvert(boolean postAdvert) {
        this.postAdvert = postAdvert;
        return this;
    }

    /**
     * Set whether the advert should post even if the player does not have funds.
     *
     * @param bypassAdvertCost whether the advert should post even if the player does not have funds.
     * @return the post.
     * @since 3.0.0
     */
    public Post bypassAdvertCost(boolean bypassAdvertCost) {
        this.bypassAdvertCost = bypassAdvertCost;
        return this;
    }

    /**
     * Set whether the listing should still be posted even if the player has reached their max listings.
     *
     * @param bypassMaxListings whether the listing should still be posted even if the player has reached their max listings.
     * @return the post.
     * @since 3.0.0
     */
    public Post bypassMaxListings(boolean bypassMaxListings) {
        this.bypassMaxListings = bypassMaxListings;
        return this;
    }

    /**
     * Set whether the listing should still be posted even if the item is restricted.
     *
     * @param bypassRestrictedItems whether the listing should still be posted even if the item is restricted.
     * @return the post.
     * @since 3.0.0
     */
    public Post bypassRestrictedItems(boolean bypassRestrictedItems) {
        this.bypassRestrictedItems = bypassRestrictedItems;
        return this;
    }

    /**
     * Set whether the player should be taxed on the sale of the listing.
     *
     * @param bypassTax whether the player should be taxed on the sale of the listing.
     * @return the post.
     * @since 3.0.0
     */
    public Post bypassTax(boolean bypassTax) {
        this.bypassTax = bypassTax;
        return this;
    }

    /**
     * Set whether the {@link ListingCreateEvent} event should be called.
     *
     * @param callEvent whether the {@link ListingCreateEvent} event should be called.
     * @return the post.
     * @since 3.0.0
     */
    public Post callEvent(boolean callEvent) {
        this.callEvent = callEvent;
        return this;
    }

    /**
     * Set whether the player should be notified about the new listing.
     *
     * @param notifyPlayer whether the player should be notified about the new listing.
     * @return the post.
     * @since 3.0.0
     */
    public Post notifyPlayer(boolean notifyPlayer) {
        this.notifyPlayer = notifyPlayer;
        return this;
    }

    /**
     * Set whether the listing should be logged into history and log file.
     *
     * @param submitLog whether the listing should be logged into history and log file.
     * @return the post.
     * @since 3.0.0
     */
    public Post submitLog(boolean submitLog) {
        this.submitLog = submitLog;
        return this;
    }

    /**
     * Set whether the players watching for a listing should get alerted.
     *
     * @param alertWatchers whether the players watching for a listing should get alerted.
     * @return the post.
     * @since 3.0.0
     */
    public Post alertWatchers(boolean alertWatchers) {
        this.alertWatchers = alertWatchers;
        return this;
    }

    /**
     * Build the post and the listing and submit it to the auction house.
     *
     * @return a {@link PostResult}
     */
    public abstract PostResult buildAndSubmit();
}
