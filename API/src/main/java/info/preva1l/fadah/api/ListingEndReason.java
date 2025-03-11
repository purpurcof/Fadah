package info.preva1l.fadah.api;

/**
 * The reason the listing was ended.
 * <br><br>
 * Created on 27/06/2024
 *
 * @author Preva1l
 */
public enum ListingEndReason {
    /**
     * The listing expired.
     */
    EXPIRED,
    /**
     * The owner cancelled the listing.
     */
    CANCELLED,
    /**
     * An admin cancelled the listing.
     */
    CANCELLED_ADMIN
}
