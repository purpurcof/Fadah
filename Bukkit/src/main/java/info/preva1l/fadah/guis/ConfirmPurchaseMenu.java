package info.preva1l.fadah.guis;

import info.preva1l.fadah.records.listing.BinListing;
import info.preva1l.fadah.utils.guis.LayoutService;
import org.bukkit.entity.Player;

public class ConfirmPurchaseMenu extends PurchaseMenu {
    public ConfirmPurchaseMenu(BinListing listing,
                               Player player,
                               Runnable returnFunction) {
        super(listing,
                listing.getPrice(),
                returnFunction,
                () -> {
                    player.closeInventory();
                    listing.purchase(player);
                },
                LayoutService.MenuType.CONFIRM_PURCHASE);
    }
}