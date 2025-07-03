package info.preva1l.fadah.security;

import info.preva1l.fadah.records.collection.CollectableItem;

/**
 * Created on 16/06/2025
 *
 * @author Preva1l
 */
public interface AwareCollectableDataProvider<T> {
    /**
     * This method only runs action if the object is safe to modify.
     */
    void execute(T box, CollectableItem collectable, Runnable action);
}
