package info.preva1l.fadah.guis;

import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.config.misc.Tuple;
import info.preva1l.fadah.records.listing.BidListing;
import info.preva1l.fadah.utils.guis.FastInv;
import info.preva1l.fadah.utils.guis.ItemBuilder;
import info.preva1l.fadah.utils.guis.LayoutService;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created on 28/04/2025
 *
 * @author Preva1l
 */
public class PlaceBidMenu extends FastInv {
    private final double amount;

    public PlaceBidMenu(BidListing listing,
                        Player player,
                        Runnable returnFunction) {
        this(listing, player, listing.getPrice() + (listing.getPrice() / 2), returnFunction);
    }

    public PlaceBidMenu(BidListing listing,
                        Player player,
                        double amount,
                        Runnable returnFunction) {
        super(LayoutService.MenuType.PLACE_BID.getLayout().guiSize(),
                LayoutService.MenuType.PLACE_BID.getLayout().formattedTitle(),
                LayoutService.MenuType.PLACE_BID);

        this.amount = amount;

        fillers();
        setItem(getLayout().buttonSlots().getOrDefault(LayoutService.ButtonType.CONFIRM, -1),
                new ItemBuilder(getLang().getAsMaterial("confirm.icon", Material.LIME_CONCRETE))
                        .name(getLang().getStringFormatted("confirm.name", "&a&lCONFIRM"))
                        .modelData(getLang().getInt("confirm.model-data"))
                        .lore(getLang().getLore("confirm.lore",
                                Tuple.of("%price%", Config.i().getFormatting().numbers().format(amount))))
                        .build(), e -> {
                    player.closeInventory();
                    listing.newBid(player, this.amount);
                });

        setItem(getLayout().buttonSlots().getOrDefault(LayoutService.ButtonType.ADJUST_BID, -1),
                new ItemBuilder(getLang().getAsMaterial("adjust-bid.icon", Material.ANVIL))
                        .name(getLang().getStringFormatted("adjust-bid.name", "&3&lAdjust Bid Amount"))
                        .modelData(getLang().getInt("adjust-bid.model-data"))
                        .lore(getLang().getLore("adjust-bid.lore",
                                Tuple.of("%current%", Config.i().getFormatting().numbers().format(listing.getPrice())),
                                Tuple.of("%bid%", Config.i().getFormatting().numbers().format(amount))))
                        .build(), e ->
                    new InputMenu<>(
                            player,
                            getLang().getString("input.title", ""),
                            getLang().getString("input.placeholder", ""),
                            Double.class,
                            a -> {
                                if (a <= listing.getPrice()) {
                                    Lang.sendMessage(player, Lang.i().getErrors().getBidTooLow());
                                    return;
                                }
                                new PlaceBidMenu(listing, player, a, returnFunction).open(player);
                            }
                    )
                );

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