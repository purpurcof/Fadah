package info.preva1l.fadah.cache.listing;

import info.preva1l.fadah.cache.Cache;
import info.preva1l.fadah.multiserver.RedisBroker;
import info.preva1l.fadah.records.listing.Listing;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.options.LocalCachedMapOptions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DistributedListingCache implements Cache<Listing> {
    private final RLocalCachedMap<UUID, Listing> listings;

    public DistributedListingCache() {
        final LocalCachedMapOptions<UUID, Listing> options = LocalCachedMapOptions.<UUID, Listing>name("listings")
                .cacheSize(0)
                .timeToLive(Duration.ZERO)
                .maxIdle(Duration.ZERO)
                .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.NONE)
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.INVALIDATE)
                .storeMode(LocalCachedMapOptions.StoreMode.LOCALCACHE_REDIS)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.LOAD)
                .expirationEventPolicy(LocalCachedMapOptions.ExpirationEventPolicy.SUBSCRIBE_WITH_KEYSPACE_CHANNEL)
                .cacheProvider(LocalCachedMapOptions.CacheProvider.REDISSON)
                .storeCacheMiss(false);

        listings = RedisBroker.getRedisson().getLocalCachedMap(options);
    }

    @Override
    public void add(Listing obj) {
        if (obj == null) return;
        listings.fastPutAsync(obj.getId(), obj);
    }

    @Override
    public Listing get(UUID uuid) {
        return listings.get(uuid);
    }

    @Override
    public void invalidate(@NotNull UUID uuid) {
        listings.fastRemoveAsync(uuid);
    }

    @Override
    public void invalidate(@NotNull Listing obj) {
        listings.fastRemoveAsync(obj.getId());
    }

    @Override
    public @NotNull List<Listing> getAll() {
        return new ArrayList<>(listings.readAllValues());
    }

    @Override
    public int size() {
        return listings.size();
    }

    @Override
    public int amountByPlayer(@NotNull UUID player) {
        return (int) getAll().stream()
                .filter(listing -> listing.isOwner(player))
                .count();
    }
}