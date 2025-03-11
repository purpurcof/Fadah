package info.preva1l.fadah.records.collection;

import java.util.List;
import java.util.UUID;

/**
 * The players collection box.
 * <br>
 * The collection box is a list of {@link CollectableItem}
 * <br><br>
 * Created on 7/06/2024
 *
 * @author Preva1l
 * @param owner the player who owns the collection box.
 * @param collectableItems the items in the collection box.
 */
public record CollectionBox(
        UUID owner,
        List<CollectableItem> collectableItems
) {
    /**
     * Add a {@link CollectableItem} to the collection box.
     *
     * @param collectableItem the item to add.
     */
    public void add(CollectableItem collectableItem) {
        collectableItems.add(collectableItem);
    }

    /**
     * Remove a {@link CollectableItem} from the collection box.
     *
     * @param collectableItem the item to remove.
     */
    public void remove(CollectableItem collectableItem) {
        collectableItems.remove(collectableItem);
    }
}
