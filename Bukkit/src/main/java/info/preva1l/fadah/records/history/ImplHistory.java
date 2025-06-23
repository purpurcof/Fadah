package info.preva1l.fadah.records.history;

import info.preva1l.fadah.data.DataService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created on 23/06/2025
 *
 * @author Preva1l
 */
public final class ImplHistory implements History {
    private final UUID owner;
    private final List<HistoricItem> historicItems;

    public ImplHistory(UUID owner, List<HistoricItem> historicItems) {
        this.owner = owner;
        this.historicItems = historicItems;
    }

    /**
     * Create a new empty history for a player.
     *
     * @param owner the player to create the history for.
     * @return the history instance.
     */
    public static History empty(UUID owner) {
        return new ImplHistory(owner, new ArrayList<>());
    }

    @Override
    public List<HistoricItem> items() {
        return historicItems;
    }

    @Override
    public UUID owner() {
        return owner;
    }

    @Override
    public boolean contains(HistoricItem historicItem) {
        return historicItems.contains(historicItem);
    }

    @Override
    public void add(HistoricItem historicItem) {
        save();
        historicItems.add(historicItem);
    }

    @Override
    public void remove(HistoricItem historicItem) {
        historicItems.remove(historicItem);
        save();
    }

    private void save() {
        DataService.instance.save(History.class, this);
    }
}
