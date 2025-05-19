package info.preva1l.fadah.records.listing;

import info.preva1l.fadah.cache.CacheAccess;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.config.misc.Tuple;
import info.preva1l.fadah.data.DataService;
import info.preva1l.fadah.multiserver.Broker;
import info.preva1l.fadah.multiserver.Message;
import info.preva1l.fadah.multiserver.Payload;
import info.preva1l.fadah.records.collection.CollectableItem;
import info.preva1l.fadah.records.collection.CollectionBox;
import info.preva1l.fadah.utils.Text;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@Getter
public final class ImplBidListing extends ActiveListing implements BidListing {
    private final double startingBid;
    private final ConcurrentSkipListSet<Bid> bids;

    public ImplBidListing(@NotNull UUID id, @NotNull UUID owner, @NotNull String ownerName,
                          @NotNull ItemStack itemStack, @NotNull String categoryID, @NotNull String currency, double startingBid,
                          double tax, long creationDate, long deletionDate, ConcurrentSkipListSet<Bid> bids) {
        super(id, owner, ownerName, itemStack, categoryID, currency, tax, creationDate, deletionDate);
        this.startingBid = startingBid;
        this.bids = bids;
    }

    @Override
    public double getPrice() {
        return getCurrentBid().bidAmount();
    }

    @Override
    public boolean canBuy(@NotNull Player player) {
        if (!getCurrency().canAfford(player, getCurrentBid().bidAmount() + 1)) {
            Lang.sendMessage(player, Lang.i().getPrefix() + Lang.i().getErrors().getTooExpensive());
            return false;
        }

        if (getCurrentBid().bidder().equals(player.getUniqueId())) {
            Lang.sendMessage(player, Lang.i().getPrefix() + Lang.i().getErrors().getAlreadyHighestBidder());
            return false;
        }

        return super.canBuy(player);
    }

    @Override
    public StaleListing getAsStale() {
        return new StaleListing(id, owner, ownerName, itemStack, categoryID, currencyId, getCurrentBid().bidAmount(), tax, creationDate, deletionDate, bids);
    }

    @Override
    public Bid getCurrentBid() {
        if (bids.isEmpty()) {
            return new Bid(
                    new UUID(0L, 0L),
                    Lang.i().getWords().getStartingBid(),
                    startingBid,
                    creationDate
            );
        }
        return bids.first();
    }

    /**
     * Add a new bid.
     *
     * @param bidder    the person bidding
     * @param bidAmount the amount of the bid
     */
    @Override
    public void newBid(@NotNull Player bidder, double bidAmount) {
        if (!canBuy(bidder)) return;

        Bid mostRecentBid = getCurrentBid();
        if (mostRecentBid.bidAmount() >= bidAmount) {
            Lang.sendMessage(bidder, Lang.i().getPrefix() + Lang.i().getErrors().getBidTooLow());
            return;
        }

        double previous = bids.stream()
                .filter(b -> b.bidder().equals(bidder.getUniqueId()))
                .findFirst()
                .map(Bid::bidAmount)
                .orElse(0D);

        getCurrency().withdraw(bidder, bidAmount - previous); // take amount minus previous bid

        bids.add(new Bid(bidder.getUniqueId(), bidder.getName(), bidAmount, System.currentTimeMillis()));

        String itemName = Text.extractItemName(itemStack);
        String formattedPrice = Config.i().getFormatting().numbers().format(bidAmount);
        Component message = Text.text(Lang.i().getNotifications().getBidPlaced(),
                Tuple.of("%item%", itemName),
                Tuple.of("%price%", formattedPrice)
        );

        bidder.sendMessage(message);

        // notify other bidders
        Set<UUID> notified = new HashSet<>();
        for (Bid bid : bids) {
            Component outbidMessage = Text.text(Lang.i().getNotifications().getOutBid(),
                    Tuple.of("%item%", itemName),
                    Tuple.of("%price%", formattedPrice)
            );
            if (notified.contains(bid.bidder()) || bid.bidder() == bidder.getUniqueId()) continue;
            Player player = Bukkit.getPlayer(bid.bidder());
            if (player != null) {
                player.sendMessage(outbidMessage);
            } else {
                if (Broker.getInstance().isConnected()) {
                    Message.builder()
                            .type(Message.Type.NOTIFICATION)
                            .payload(Payload.withNotification(bid.bidder(), outbidMessage))
                            .build().send(Broker.getInstance());
                }
            }
            notified.add(bid.bidder());
        }

        CacheAccess.add(Listing.class, this);
    }

    @Override
    public void completeBidding() {
        if (bids.isEmpty()) {
            expire();
            return;
        }
        Bid winningBid = bids.first();

        // Money Transfer
        double taxed = (this.getTax()/100) * winningBid.bidAmount();
        getCurrency().add(Bukkit.getOfflinePlayer(this.getOwner()), winningBid.bidAmount() - taxed);

        // refund
        Set<UUID> refunded = new HashSet<>();
        for (Bid bid : bids) {
            if (refunded.contains(bid.bidder()) || bid.equals(winningBid)) continue; // only refund their latest bid

            getCurrency().add(Bukkit.getOfflinePlayer(bid.bidder()), bid.bidAmount());
            refunded.add(bid.bidder());
        }

        // Remove Listing
        CacheAccess.invalidate(Listing.class, this);
        DataService.getInstance().delete(Listing.class, this);

        // Add to collection box
        ItemStack itemStack = this.getItemStack().clone();
        CacheAccess.get(CollectionBox.class, winningBid.bidder())
                .ifPresentOrElse(
                        cache -> cache.add(new CollectableItem(itemStack, System.currentTimeMillis())),
                        () -> DataService.getInstance()
                                .get(CollectionBox.class, winningBid.bidder())
                                .thenCompose(items -> {
                                    var box = items.orElseGet(() -> CollectionBox.empty(winningBid.bidder()));
                                    return DataService.getInstance().save(CollectionBox.class, box);
                                })
                );

        // Notify Both Players
        Player onlineBuyer = Bukkit.getPlayer(winningBid.bidder());
        OfflinePlayer buyer = Bukkit.getOfflinePlayer(winningBid.bidder());
        if (onlineBuyer != null) {
            onlineBuyer.sendMessage(Text.text(Lang.i().getNotifications().getNewItem()));
        } else {
            if (Broker.getInstance().isConnected()) {
                Message.builder()
                        .type(Message.Type.NOTIFICATION)
                        .payload(Payload.withNotification(winningBid.bidder(), Text.text(Lang.i().getNotifications().getNewItem())))
                        .build().send(Broker.getInstance());
            }
        }

        String itemName = Text.extractItemName(itemStack);
        String formattedPrice = Config.i().getFormatting().numbers().format(winningBid.bidAmount() - taxed);
        Component message = Text.text(Lang.i().getNotifications().getSale(),
                Tuple.of("%item%", itemName),
                Tuple.of("%price%", formattedPrice),
                Tuple.of("%buyer%", winningBid.bidderName()));

        complete(message, buyer);
    }
}
