package info.preva1l.fadah.cache.collectionbox;

import info.preva1l.fadah.cache.Cache;
import info.preva1l.fadah.multiserver.RedisBroker;
import info.preva1l.fadah.records.collection.CollectionBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.options.LocalCachedMapOptions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DistributedCollectionCache implements Cache<CollectionBox> {
    private final RLocalCachedMap<UUID, CollectionBox> collectionBoxes;

    public DistributedCollectionCache() {
        final LocalCachedMapOptions<UUID, CollectionBox> options = LocalCachedMapOptions.<UUID, CollectionBox>name("collection-boxes")
                .cacheSize(0)
                .timeToLive(Duration.ZERO)
                .maxIdle(Duration.ZERO)
                .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.NONE)
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .storeMode(LocalCachedMapOptions.StoreMode.LOCALCACHE_REDIS)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.LOAD)
                .expirationEventPolicy(LocalCachedMapOptions.ExpirationEventPolicy.SUBSCRIBE_WITH_KEYEVENT_PATTERN)
                .cacheProvider(LocalCachedMapOptions.CacheProvider.REDISSON)
                .storeCacheMiss(false);

        collectionBoxes = RedisBroker.getRedisson().getLocalCachedMap(options);
    }

    @Override
    public void add(@Nullable CollectionBox obj) {
        if (obj == null) return;
        collectionBoxes.fastPutAsync(obj.owner(), obj);
    }

    @Override
    public @Nullable CollectionBox get(UUID uuid) {
        return collectionBoxes.get(uuid);
    }

    @Override
    public void invalidate(@NotNull UUID uuid) {
        collectionBoxes.fastRemoveAsync(uuid);
    }

    @Override
    public void invalidate(@NotNull CollectionBox obj) {
        collectionBoxes.fastRemoveAsync(obj.owner());
    }

    @Override
    public @NotNull List<CollectionBox> getAll() {
        return new ArrayList<>(collectionBoxes.readAllValues());
    }

    @Override
    public int size() {
        return collectionBoxes.size();
    }

    @Override
    public int amountByPlayer(@NotNull UUID player) {
        return collectionBoxes.get(player).collectableItems().size();
    }
}
