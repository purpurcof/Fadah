package info.preva1l.fadah.records.listing;

import info.preva1l.fadah.cache.CacheAccess;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.config.misc.Tuple;
import info.preva1l.fadah.data.DataService;
import info.preva1l.fadah.records.collection.CollectableItem;
import info.preva1l.fadah.records.collection.CollectionBox;
import info.preva1l.fadah.security.AwareDataService;
import info.preva1l.fadah.utils.Text;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@Getter
public final class ImplBinListing extends ActiveListing implements BinListing {
    private final double price;

    public ImplBinListing(@NotNull UUID id, @NotNull UUID owner, @NotNull String ownerName,
                          @NotNull ItemStack itemStack, @NotNull String categoryID, @NotNull String currency, double price,
                          double tax, long creationDate, long deletionDate) {
        super(id, owner, ownerName, itemStack, categoryID, currency, tax, creationDate, deletionDate);
        this.price = price;
    }

    @Override
    public boolean canBuy(@NotNull Player player) {
        if (!getCurrency().canAfford(player, getPrice())) {
            Lang.sendMessage(player, Lang.i().getPrefix() + Lang.i().getErrors().getTooExpensive());
            return false;
        }

        return super.canBuy(player);
    }

    @Override
    public StaleListing getAsStale() {
        return new StaleListing(id, owner, ownerName, itemStack, categoryID, currencyId, price, tax, creationDate, deletionDate, new ConcurrentSkipListSet<>());
    }

    @Override
    public void purchase(@NotNull Player buyer) {
        AwareDataService.instance.execute(Listing.class, this, () -> purchase0(buyer));
    }

    private void purchase0(@NotNull Player buyer) {
        if (!canBuy(buyer)) return;

        double taxedAmount = (this.getTax() / 100) * this.getPrice();
        double sellerAmount = this.getPrice() - taxedAmount;

        try {
            if (!transferFunds(buyer, sellerAmount)) {
                return;
            }

            ItemStack itemStack = this.getItemStack().clone();
            CacheAccess.getNotNull(CollectionBox.class, buyer.getUniqueId())
                    .add(new CollectableItem(itemStack, Instant.now().toEpochMilli()));

            removeListing();

            sendNotifications(buyer, itemStack, sellerAmount);

        } catch (Exception e) {
            System.err.println("Purchase failed for listing " + getId() + ": " + e.getMessage());
            rollbackTransaction(buyer, sellerAmount);
            throw new RuntimeException("Purchase transaction failed", e);
        }
    }

    private boolean transferFunds(@NotNull Player buyer, double sellerAmount) {
        if (!getCurrency().canAfford(buyer, this.getPrice())) {
            Lang.sendMessage(buyer, Lang.i().getPrefix() + Lang.i().getErrors().getTooExpensive());
            return false;
        }

        if (!getCurrency().withdraw(buyer, this.getPrice())) {
            Lang.sendMessage(buyer, Lang.i().getPrefix() + "Transaction failed: Unable to withdraw funds.");
            return false;
        }

        try {
            if (!getCurrency().add(Bukkit.getOfflinePlayer(this.getOwner()), sellerAmount)) {
                getCurrency().add(buyer, this.getPrice());
                Lang.sendMessage(buyer, Lang.i().getPrefix() + "Transaction failed: Unable to pay seller.");
                return false;
            }
        } catch (Exception e) {
            getCurrency().add(buyer, this.getPrice());
            throw e;
        }

        return true;
    }

    private void removeListing() {
        CacheAccess.invalidate(Listing.class, this);
        DataService.getInstance().delete(Listing.class, this);
    }

    private void sendNotifications(@NotNull Player buyer, ItemStack itemStack, double sellerAmount) {
        Lang.sendMessage(buyer, String.join("\n", Lang.i().getNotifications().getNewItem()));

        String itemName = Text.extractItemName(itemStack);
        String formattedPrice = Config.i().getFormatting().numbers().format(sellerAmount);
        Component message = Text.text(Lang.i().getNotifications().getSale(),
                Tuple.of("%item%", itemName),
                Tuple.of("%price%", formattedPrice),
                Tuple.of("%buyer%", buyer.getName()));

        complete(message, buyer);
    }

    private void rollbackTransaction(@NotNull Player buyer, double sellerAmount) {
        try {
            getCurrency().add(buyer, this.getPrice());
            getCurrency().withdraw(Bukkit.getOfflinePlayer(this.getOwner()), sellerAmount);
        } catch (Exception rollbackException) {
            System.err.println("CRITICAL: Rollback failed for listing " + getId() +
                    " - manual intervention required: " + rollbackException.getMessage());
        }
    }
}