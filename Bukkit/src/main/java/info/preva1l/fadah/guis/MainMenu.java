package info.preva1l.fadah.guis;

import info.preva1l.fadah.cache.CacheAccess;
import info.preva1l.fadah.cache.CategoryRegistry;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.filters.SortingDirection;
import info.preva1l.fadah.filters.SortingMethod;
import info.preva1l.fadah.records.Category;
import info.preva1l.fadah.records.listing.Listing;
import info.preva1l.fadah.utils.Text;
import info.preva1l.fadah.utils.guis.ItemBuilder;
import info.preva1l.fadah.utils.guis.LayoutManager;
import info.preva1l.fadah.utils.guis.PaginatedItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MainMenu extends PurchaseMenu {
    private Category category;

    public MainMenu(
            @Nullable Category category,
            @NotNull Player player,
            @Nullable String search,
            @Nullable SortingMethod sortingMethod,
            @Nullable SortingDirection sortingDirection) {
        super(
                player,
                LayoutManager.MenuType.MAIN.getLayout().guiTitle(),
                LayoutManager.MenuType.MAIN,
                () -> CacheAccess.getAll(Listing.class),
                search,
                sortingMethod,
                sortingDirection
        );
        this.category = category;

        if (category != null) {
            listings.removeIf(listing -> !listing.getCategoryID().equals(category.id()));
        }
    }

    @Override
    public void fillScrollbarItems() {
        for (Category cat : CategoryRegistry.getCategories()) {
            ItemBuilder itemBuilder = new ItemBuilder(cat.icon())
                    .name(Text.modernMessage(cat.name()))
                    .addLore(Text.modernList(cat.description()))
                    .modelData(cat.modelData())
                    .setAttributes(null)
                    .flags(ItemFlag.HIDE_ENCHANTS,
                            ItemFlag.HIDE_ATTRIBUTES,
                            ItemFlag.HIDE_UNBREAKABLE,
                            ItemFlag.HIDE_DESTROYS,
                            ItemFlag.HIDE_PLACED_ON,
                            ItemFlag.HIDE_DYE,
                            ItemFlag.HIDE_POTION_EFFECTS);
            if (category == cat) {
                itemBuilder.name(Text.modernMessage(cat.name() + "&r " + Lang.i().getCategorySelected()))
                        .enchant(Enchantment.DURABILITY);
                itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
            }

            addScrollbarItem(new PaginatedItem(itemBuilder.build(), e -> {
                if (category != cat) {
                    this.category = cat;
                } else {
                    this.category = null;
                }

                updatePagination();
                updateScrollbar();
            }));
        }
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
                                new MainMenu(category, player, search, sortingMethod, sortingDirection).open(player)));
    }

    protected void addNavigationButtons() {
        super.addNavigationButtons();

        setItem(getLayout().buttonSlots().getOrDefault(LayoutManager.ButtonType.PROFILE,-1),
                new ItemBuilder(getLang().getAsMaterial("profile-button.icon", Material.PLAYER_HEAD)).skullOwner(player)
                        .name(getLang().getStringFormatted("profile-button.name", "&e&lYour Profile"))
                        .addLore(getLang().getLore("profile-button.lore"))
                        .modelData(getLang().getInt("profile-button.model-data"))
                        .build(), e -> new ProfileMenu(player, player).open(player));
    }

    @Override
    protected void updatePagination() {
        super.updatePagination();

        if (category != null) {
            listings.removeIf(listing -> !listing.getCategoryID().equals(category.id()));
        }
    }
}
