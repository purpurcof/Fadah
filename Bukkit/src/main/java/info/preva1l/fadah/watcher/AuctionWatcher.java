package info.preva1l.fadah.watcher;

import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.records.Listing;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class AuctionWatcher {
    @Getter private final Map<UUID, Watching> watchingListings = new ConcurrentHashMap<>();

    public void watch(@NotNull Watching watching) {
        watchingListings.put(watching.getPlayer(), watching);
    }

    @Blocking
    public void alertWatchers(@NotNull Listing listing) {
        for (Map.Entry<UUID, Watching> entry : watchingListings.entrySet()) {
            Watching watching = entry.getValue();
            if (watching.getSearch() != null) {
                if (checkForStringInItem(watching.getSearch().toUpperCase(), listing.getItemStack())
                        || checkForEnchantmentOnBook(watching.getSearch().toUpperCase(), listing.getItemStack())) {
                    if ((watching.getMinPrice() != -1 || watching.getMaxPrice() != -1) &&
                            (watching.getMinPrice() == -1 || watching.getMinPrice() <= listing.getPrice()) &&
                            (watching.getMaxPrice() == -1 || watching.getMaxPrice() >= listing.getPrice())) {
                        sendAlert(entry.getKey(), listing);
                        return;
                    }
                    sendAlert(entry.getKey(), listing);
                    return;
                }
            }

            if ((watching.getMinPrice() != -1 || watching.getMaxPrice() != -1) &&
                    (watching.getMinPrice() == -1 || watching.getMinPrice() <= listing.getPrice()) &&
                    (watching.getMaxPrice() == -1 || watching.getMaxPrice() >= listing.getPrice())) {
                sendAlert(entry.getKey(), listing);
            }
        }
    }

    private void sendAlert(UUID uuid, Listing listing) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        player.sendMessage("Skibidi Skibidi Hawk Tuah Hawk");
    }

    private boolean checkForEnchantmentOnBook(String enchant, ItemStack enchantedBook) {
        if (!Config.i().getSearch().isEnchantedBooks()) return false;
        if (enchantedBook.getType() == Material.ENCHANTED_BOOK) {
            for (Enchantment enchantment : enchantedBook.getEnchantments().keySet()) {
                if (enchantment.getKey().getKey().toUpperCase().contains(enchant)) return true;
            }
        }
        return false;
    }

    private boolean checkForStringInItem(String toCheck, ItemStack item) {
        if (Config.i().getSearch().isType()) {
            if (item.getType().name().toUpperCase().contains(toCheck.toUpperCase())
                    || item.getType().name().toUpperCase().contains(toCheck.replace(" ", "_").toUpperCase())) {
                return true;
            }
        }

        if (item.getItemMeta() != null) {
            if (Config.i().getSearch().isName()) {
                if (item.getItemMeta().getDisplayName().toUpperCase().contains(toCheck.toUpperCase())) {
                    return true;
                }
            }

            if (Config.i().getSearch().isLore()) {
                return item.getItemMeta().getLore() != null && item.getItemMeta().getLore().contains(toCheck.toUpperCase());
            }
        }
        return false;
    }
}
