package info.preva1l.fadah.records;

import java.util.List;
import java.util.UUID;

/**
 * <p>
 *     Holds cached data and is saved to a database.
 * </p>
 * Created on 23/06/2025
 *
 * @author Preva1l
 */
public interface StorageHolder<ITEM> {
    /**
     * The items in the storage holder.
     *
     * @return items.
     */
    List<ITEM> items();

    /**
     * The player who owns the storage holder.
     *
     * @return the owner.
     */
    UUID owner();

    /**
     * Check if the storage holder contains an item.
     *
     * @param item the item to check.
     * @return true if the holder has the item.
     */
    boolean contains(ITEM item);

    /**
     * Add an item to the storage holder
     *
     * @param item item to add.
     */
    void add(ITEM item);

    /**
     * Remove an item from the storage holder.
     *
     * @param item item to remove.
     */
    void remove(ITEM item);
}
