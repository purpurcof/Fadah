package info.preva1l.fadah.security.impl;

import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.multiserver.RedisBroker;
import info.preva1l.fadah.records.collection.CollectionBox;
import info.preva1l.fadah.security.AwareDataProvider;
import lombok.AllArgsConstructor;

import java.util.concurrent.ExecutorService;

/**
 * Created on 16/06/2025
 *
 * @author Preva1l
 */
@AllArgsConstructor
public final class CollectionBoxAwareDataProvider implements AwareDataProvider<CollectionBox> {
    private final ExecutorService executor;

    @Override
    public void execute(CollectionBox box, Runnable action) {
        if (Config.i().getBroker().isEnabled() && RedisBroker.getInstance() != null) {
            RedisBroker.getRedisson().getFairLock(box.owner().toString())
                    .tryLockAsync()
                    .thenAcceptAsync(locked -> action.run(), executor);
            return;
        }

        action.run();
    }
}
