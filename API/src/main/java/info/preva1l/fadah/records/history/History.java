package info.preva1l.fadah.records.history;

import java.util.List;
import java.util.UUID;

/**
 * The player's history.
 * <p>
 * History is a list of {@link HistoricItem}
 * <br><br>
 * Created on 13/04/2024
 *
 * @author Preva1l
 * @param owner the player who owns the history.
 * @param historicItems the items in the history.
 */
public record History(
        UUID owner,
        List<HistoricItem> historicItems
) {
    /**
     * Add a {@link HistoricItem} to the history.
     *
     * @param historicItem the item to add.
     */
    public void add(HistoricItem historicItem) {
        historicItems.add(historicItem);
    }

    /**
     * Remove a {@link HistoricItem} from the history.
     *
     * @param historicItem the item to remove.
     */
    public void remove(HistoricItem historicItem) {
        historicItems.remove(historicItem);
    }
}
