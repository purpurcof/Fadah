package info.preva1l.fadah.guis;

import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.config.misc.Tuple;
import info.preva1l.fadah.records.listing.BinListing;
import info.preva1l.fadah.utils.guis.FastInv;
import info.preva1l.fadah.utils.guis.ItemBuilder;
import info.preva1l.fadah.utils.guis.LayoutService;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ConfirmPurchaseMenu extends FastInv {
    public ConfirmPurchaseMenu(BinListing listing,
                               Player player,
                               Runnable returnFunction) {
        super(LayoutService.MenuType.CONFIRM_PURCHASE.getLayout().guiSize(),
                LayoutService.MenuType.CONFIRM_PURCHASE.getLayout().formattedTitle(),
                LayoutService.MenuType.CONFIRM_PURCHASE);

        fillers();
        setItem(getLayout().buttonSlots().getOrDefault(LayoutService.ButtonType.CONFIRM, -1),
                new ItemBuilder(getLang().getAsMaterial("confirm.icon", Material.LIME_CONCRETE))
                        .name(getLang().getStringFormatted("confirm.name", "&a&lCONFIRM"))
                        .modelData(getLang().getInt("confirm.model-data"))
                        .lore(getLang().getLore("confirm.lore",
                                Tuple.of("%price%", Config.i().getFormatting().numbers().format(listing.getPrice()))))
                        .build(), e -> {
            player.closeInventory();
            listing.purchase(((Player) e.getWhoClicked()));
        });

        setItem(getLayout().buttonSlots().getOrDefault(LayoutService.ButtonType.CANCEL, -1),
                new ItemBuilder(getLang().getAsMaterial("cancel.icon", Material.RED_CONCRETE))
                        .name(getLang().getStringFormatted("cancel.name", "&c&lCANCEL"))
                        .modelData(getLang().getInt("cancel.model-data"))
                        .lore(getLang().getLore("cancel.lore")).build(), e -> {
            returnFunction.run();
        });

        setItem(getLayout().buttonSlots().getOrDefault(LayoutService.ButtonType.ITEM_TO_PURCHASE, -1),
                listing.getItemStack().clone());
    }
}
