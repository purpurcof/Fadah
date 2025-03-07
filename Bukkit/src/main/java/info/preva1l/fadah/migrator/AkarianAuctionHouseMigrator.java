package info.preva1l.fadah.migrator;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.cache.CategoryRegistry;
import info.preva1l.fadah.records.collection.CollectableItem;
import info.preva1l.fadah.records.listing.BinListing;
import info.preva1l.fadah.records.listing.Listing;
import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.listings.ListingManager;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class AkarianAuctionHouseMigrator implements Migrator {
    private final String migratorName = "AkarianAuctionHouse";
    private final ListingManager listingManager = AuctionHouse.getInstance().getListingManager();

    @Override
    public List<Listing> migrateListings() {
        ArrayList<net.akarian.auctionhouse.listings.Listing> oldListings = this.listingManager.getActive();
        List<Listing> listings = new ArrayList<>();

        for (net.akarian.auctionhouse.listings.Listing oldListing : oldListings) {
            UUID id = oldListing.getId();
            UUID owner = oldListing.getCreator();
            String ownerName = "Unknown Seller";
            ItemStack item = oldListing.getItemStack();
            double price = oldListing.getPrice();

            String categoryId = CategoryRegistry.getCategoryForItem(item).join();

            long expiry = oldListing.getEnd();
            listings.add(new BinListing(id, owner, ownerName, item, categoryId, "vault", price, 0,
                    Instant.now().toEpochMilli(), expiry, new TreeSet<>()));
        }

        return listings;
    }

    @Override
    public Map<UUID, List<CollectableItem>> migrateCollectionBoxes() {
        Fadah.getConsole().warning("Not migrating collection boxes! (AkarianAuctionHouse does not permit)");
        return Collections.emptyMap();
    }

    @Override
    public Map<UUID, List<CollectableItem>> migrateExpiredListings() {
        ArrayList<net.akarian.auctionhouse.listings.Listing> expired = this.listingManager.getExpired();
        Map<UUID, List<CollectableItem>> allItems = new ConcurrentHashMap<>();

        for (net.akarian.auctionhouse.listings.Listing listing : expired) {
            UUID owner = listing.getCreator();
            ItemStack itemStack = listing.getItemStack();
            CollectableItem item = new CollectableItem(itemStack, Instant.now().toEpochMilli());

            allItems.compute(owner, (uuid, items) -> {
                if (items == null) {
                    items = new ArrayList<>();
                }

                items.add(item);
                return items;
            });
        }

        return allItems;
    }
}
