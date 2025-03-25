package info.preva1l.fadah.guis;

import info.preva1l.fadah.cache.CacheAccess;
import info.preva1l.fadah.config.Menus;
import info.preva1l.fadah.filters.SortingDirection;
import info.preva1l.fadah.filters.SortingMethod;
import info.preva1l.fadah.records.listing.Listing;
import info.preva1l.fadah.utils.guis.ItemBuilder;
import info.preva1l.fadah.utils.guis.LayoutManager;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ViewListingsMenu extends PurchaseMenu {
    private final OfflinePlayer owner;

    public ViewListingsMenu(
            @NotNull Player player,
            OfflinePlayer owner,
            LayoutManager.MenuType type,
            @Nullable String search,
            @Nullable SortingMethod sortingMethod,
            @Nullable SortingDirection sortingDirection) {
        super(
                player,
                type.getLayout().guiTitle(),
                type,
                () -> {
                    List<Listing> listings = CacheAccess.getAll(Listing.class);
                    listings.removeIf(listing -> !listing.isOwner(owner.getUniqueId()));
                    return listings;
                },
                search,
                sortingMethod,
                sortingDirection
        );

        this.owner = owner;
    }

    protected void addFilterButtons() {
        super.addFilterButtons();

        // Search
        removeItem(getLayout().buttonSlots().getOrDefault(LayoutManager.ButtonType.SEARCH,-1));
        setItem(getLayout().buttonSlots().getOrDefault(LayoutManager.ButtonType.SEARCH,-1),
                new ItemBuilder(getLang().getAsMaterial("filter.search.icon", Material.OAK_SIGN))
                        .name(getLang().getStringFormatted("filter.search.name", "&3&lSearch"))
                        .modelData(getLang().getInt("filter.search.model-data"))
                        .lore(getLang().getLore("filter.search.lore")).build(), e ->
                        new SearchMenu(player, getLang().getString("filter.search.placeholder", "Search Query..."), search ->
                                new ViewListingsMenu(player, owner, getMenuType(), search, sortingMethod, sortingDirection).open(player)));
    }

    protected void addNavigationButtons() {
        super.addNavigationButtons();

        setItem(getLayout().buttonSlots().getOrDefault(LayoutManager.ButtonType.CLOSE, -1),
                Menus.i().getCloseButton().itemStack(), e -> e.getWhoClicked().closeInventory());
    }

    @Override
    protected void fillScrollbarItems() {
        // do nothing
    }
}
