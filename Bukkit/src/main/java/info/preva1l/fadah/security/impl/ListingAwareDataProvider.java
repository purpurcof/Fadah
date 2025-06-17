package info.preva1l.fadah.security.impl;

import info.preva1l.fadah.cache.CacheAccess;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.data.DataService;
import info.preva1l.fadah.multiserver.RedisBroker;
import info.preva1l.fadah.records.listing.Listing;
import info.preva1l.fadah.security.AwareDataProvider;
import lombok.AllArgsConstructor;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * Created on 16/06/2025
 *
 * @author Preva1l
 */
@AllArgsConstructor
public final class ListingAwareDataProvider implements AwareDataProvider<Listing> {
    private final ExecutorService executor;

    @Override
    public void execute(Listing listing, Runnable action) {
        if (Config.i().getBroker().isEnabled() && RedisBroker.getInstance() != null) {
            RedisBroker.getRedisson().getFairLock(listing.getId().toString())
                    .tryLockAsync()
                    .thenAcceptAsync(locked -> checkCache(listing.getId(), action), executor);
            return;
        }
        checkCache(listing.getId(), action);
    }

    private void checkCache(UUID id, Runnable action) {
        if (CacheAccess.get(Listing.class, id).isEmpty()) return;
        checkDatabase(id, action);
    }

    private void checkDatabase(UUID id, Runnable action) {
        DataService.instance.get(Listing.class, id).thenAcceptAsync(listing -> {
            if (listing.isEmpty()) {
                CacheAccess.invalidate(Listing.class, id);
                return;
            }
            action.run();
        }, executor);
    }
}
