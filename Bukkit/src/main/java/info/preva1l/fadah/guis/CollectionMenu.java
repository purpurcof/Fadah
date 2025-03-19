package info.preva1l.fadah.guis;

import com.github.puregero.multilib.MultiLib;
import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.cache.CacheAccess;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.records.collection.CollectableItem;
import info.preva1l.fadah.records.collection.CollectionBox;
import info.preva1l.fadah.records.collection.ExpiredItems;
import info.preva1l.fadah.records.history.HistoricItem;
import info.preva1l.fadah.records.history.History;
import info.preva1l.fadah.utils.StringUtils;
import info.preva1l.fadah.utils.TimeUtil;
import info.preva1l.fadah.utils.guis.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.List;

/**
 * Created on 19/03/2025
 *
 * @author Preva1l
 */
public class CollectionMenu extends PaginatedFastInv {
    protected final OfflinePlayer owner;
    protected final boolean expired;

    public CollectionMenu(Player viewer, OfflinePlayer owner, LayoutManager.MenuType menuType) {
        super(
                menuType.getLayout().guiSize(),
                menuType.getLayout().formattedTitle(viewer.getUniqueId() == owner.getUniqueId()
                        ? StringUtils.capitalize(Lang.i().getWords().getYour())
                        : owner.getName()+"'s", owner.getName()+"'s"),
                viewer,
                menuType,
                List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34)
        );

        this.owner = owner;
        this.expired = menuType == LayoutManager.MenuType.EXPIRED_LISTINGS;

        fillers();
        setPaginationMappings(getLayout().paginationSlots());
        addNavigationButtons();
        fillPaginationItems();
        populatePage();
        addPaginationControls();
    }

    @Override
    protected synchronized void fillPaginationItems() {
        var items = expired
                ? CacheAccess.getNotNull(ExpiredItems.class, owner.getUniqueId()).expiredItems()
                : CacheAccess.getNotNull(CollectionBox.class, owner.getUniqueId()).collectableItems();
        for (CollectableItem expiredItem : items) {
            ItemBuilder itemStack = new ItemBuilder(expiredItem.itemStack().clone())
                    .addLore(getLang().getLore("lore", TimeUtil.formatTimeSince(expiredItem.dateAdded())));

            addPaginationItem(new PaginatedItem(itemStack.build(), e -> {
                MultiLib.getEntityScheduler(player).execute(Fadah.getINSTANCE(), () -> {
                    int slot = player.getInventory().firstEmpty();
                    if (slot == -1) {
                        Lang.sendMessage(player, Lang.i().getPrefix() + Lang.i().getErrors().getInventoryFull());
                        return;
                    }
                    removeFromCache(expiredItem);
                    player.getInventory().setItem(slot, expiredItem.itemStack());

                    updatePagination();

                    // In game logs
                    boolean isAdmin = player.getUniqueId() != owner.getUniqueId();
                    HistoricItem.LoggedAction action = expired
                            ? isAdmin ? HistoricItem.LoggedAction.EXPIRED_ITEM_ADMIN_CLAIM : HistoricItem.LoggedAction.EXPIRED_ITEM_CLAIM
                            : isAdmin ? HistoricItem.LoggedAction.COLLECTION_BOX_ADMIN_CLAIM : HistoricItem.LoggedAction.COLLECTION_BOX_CLAIM;
                    HistoricItem historicItem = new HistoricItem(Instant.now().toEpochMilli(),
                            action,
                            expiredItem.itemStack(), null, null);
                    CacheAccess.getNotNull(History.class, owner.getUniqueId()).add(historicItem);
                }, null, 0L);
            }));

        }
    }

    private void removeFromCache(CollectableItem item) {
        if (expired) {
            CacheAccess.getNotNull(ExpiredItems.class, owner.getUniqueId()).remove(item);
        } else {
            CacheAccess.getNotNull(CollectionBox.class, owner.getUniqueId()).remove(item);
        }
    }

    protected void addNavigationButtons() {
        setItem(getLayout().buttonSlots().getOrDefault(LayoutManager.ButtonType.BACK, -1),
                GuiHelper.constructButton(GuiButtonType.BACK), e ->
                        new ProfileMenu(player, owner).open(player));
    }
}
