package info.preva1l.fadah.records.listing;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.api.ListingEndEvent;
import info.preva1l.fadah.api.ListingEndReason;
import info.preva1l.fadah.cache.ExpiredListingsCache;
import info.preva1l.fadah.cache.ListingCache;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.data.DatabaseManager;
import info.preva1l.fadah.multiserver.Message;
import info.preva1l.fadah.multiserver.Payload;
import info.preva1l.fadah.records.CollectableItem;
import info.preva1l.fadah.records.ExpiredItems;
import info.preva1l.fadah.utils.logging.TransactionLogger;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.util.UUID;

public interface ListingExpiryProvider {
    default Runnable listingExpiryTask() {
        return () -> {
            for (UUID key : ListingCache.getListings().keySet()) {
                Listing listing = ListingCache.getListing(key);
                if (listing == null) continue;
                if (Instant.now().toEpochMilli() >= listing.getDeletionDate()) {
                    ListingCache.removeListing(listing);
                    if (Config.i().getBroker().isEnabled()) {
                        Message.builder()
                                .type(Message.Type.LISTING_REMOVE)
                                .payload(Payload.withUUID(listing.getId()))
                                .build().send(Fadah.getINSTANCE().getBroker());
                    }
                    DatabaseManager.getInstance().delete(Listing.class, listing);

                    CollectableItem collectableItem = new CollectableItem(listing.getItemStack(), Instant.now().toEpochMilli());
                    ExpiredItems items = ExpiredItems.of(listing.getOwner());
                    items.collectableItems().add(collectableItem);
                    ExpiredListingsCache.addItem(listing.getOwner(), collectableItem);
                    DatabaseManager.getInstance().save(ExpiredItems.class, items);

                    if (Config.i().getBroker().isEnabled()) {
                        Message.builder()
                                .type(Message.Type.EXPIRED_LISTINGS_UPDATE)
                                .payload(Payload.withUUID(listing.getOwner()))
                                .build().send(Fadah.getINSTANCE().getBroker());
                    }

                    TransactionLogger.listingExpired(listing);

                    Bukkit.getServer().getPluginManager().callEvent(new ListingEndEvent(listing, ListingEndReason.EXPIRED));
                }
            }
        };
    }
}
