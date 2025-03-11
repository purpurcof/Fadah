package info.preva1l.fadah.records.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The player's expired listings.
 * <p>
 * Expired items is a list of {@link CollectableItem}
 * <br><br>
 * Created on 7/06/2024
 *
 * @author Preva1l
 * @param owner the player who owns the expired items.
 * @param expiredItems the items in expired items.
 */
public record ExpiredItems(
        UUID owner,
        List<CollectableItem> expiredItems
) {
    /**
     * Create a new empty expired items for a player.
     *
     * @param owner the player to create the expired items for.
     * @return the expired items instance.
     */
    public static ExpiredItems empty(UUID owner) {
        return new ExpiredItems(owner, new ArrayList<>());
    }

    /**
     * Add a {@link CollectableItem} to the expired items.
     *
     * @param collectableItem the item to add.
     */
    public void add(CollectableItem collectableItem) {
        expiredItems.add(collectableItem);
    }

    /**
     * Remove a {@link CollectableItem} from the expired items.
     *
     * @param collectableItem the item to remove.
     */
    public void remove(CollectableItem collectableItem) {
        expiredItems.remove(collectableItem);
    }
}
