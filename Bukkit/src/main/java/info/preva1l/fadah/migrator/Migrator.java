package info.preva1l.fadah.migrator;

import info.preva1l.fadah.cache.CacheAccess;
import info.preva1l.fadah.data.DataService;
import info.preva1l.fadah.records.collection.CollectableItem;
import info.preva1l.fadah.records.collection.CollectionBox;
import info.preva1l.fadah.records.collection.ExpiredItems;
import info.preva1l.fadah.records.collection.ImplCollectionBox;
import info.preva1l.fadah.records.listing.Listing;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Migrator {
    String getMigratorName();

    default CompletableFuture<Void> startMigration() {
        return CompletableFuture.supplyAsync(() -> {
            int migratedListings = 0;
            List<Listing> listings = migrateListings();

            for (Listing listing : listings) {
                CacheAccess.add(Listing.class, listing);
                DataService.getInstance().save(Listing.class, listing);
                migratedListings++;
            }
            MigrationService.instance.logger.info("Migrated %s listings!".formatted(migratedListings));

            int migratedCollectionBoxes = 0;
            Map<UUID, List<CollectableItem>> collectionBoxes = migrateCollectionBoxes();

            for (UUID owner : collectionBoxes.keySet()) {
                CollectionBox box = DataService.getInstance().get(CollectionBox.class, owner).join()
                        .orElse(ImplCollectionBox.empty(owner));
                box.items().addAll(collectionBoxes.get(owner));
                DataService.getInstance().save(CollectionBox.class, box);
                migratedCollectionBoxes++;
            }
            MigrationService.instance.logger.info("Migrated %s collection boxes!".formatted(migratedCollectionBoxes));

            int migratedExpiredItems = 0;
            Map<UUID, List<CollectableItem>> expiredItems = migrateExpiredListings();

            for (UUID owner : expiredItems.keySet()) {
                for (CollectableItem item : expiredItems.get(owner)) {
                    CacheAccess.getNotNull(ExpiredItems.class, owner).add(item);
                }
                migratedExpiredItems++;
            }
            MigrationService.instance.logger.info("Migrated %s players expired items!".formatted(migratedExpiredItems));
            return null;
        });
    }

    List<Listing> migrateListings();
    Map<UUID, List<CollectableItem>> migrateCollectionBoxes();
    Map<UUID, List<CollectableItem>> migrateExpiredListings();
}
