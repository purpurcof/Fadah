package info.preva1l.fadah.security;

import info.preva1l.fadah.records.collection.CollectionBox;
import info.preva1l.fadah.records.collection.ExpiredItems;
import info.preva1l.fadah.records.listing.Listing;
import info.preva1l.fadah.security.impl.CollectionBoxAwareDataProvider;
import info.preva1l.fadah.security.impl.ExpiredListingsAwareDataProvider;
import info.preva1l.fadah.security.impl.ListingAwareDataProvider;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A service for modifying data that is aware of updates from other servers and prevents desync.
 * <p>
 * Created on 16/06/2025
 *
 * @author Preva1l
 */
@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AwareDataService {
    public static final AwareDataService instance = new AwareDataService();

    private final Map<Class<?>, AwareDataProvider<?>> providers = new HashMap<>();

    @Configure
    public void configure() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        providers.put(Listing.class, new ListingAwareDataProvider(executor));
        providers.put(CollectionBox.class, new CollectionBoxAwareDataProvider(executor));
        providers.put(ExpiredItems.class, new ExpiredListingsAwareDataProvider(executor));
    }

    public <T> void execute(Class<T> clazz, T obj, Runnable action) {
        getProvider(clazz).execute(obj, action);
    }

    private <T> AwareDataProvider<T> getProvider(Class<T> clazz) {
        return (AwareDataProvider<T>) providers.get(clazz);
    }
}
