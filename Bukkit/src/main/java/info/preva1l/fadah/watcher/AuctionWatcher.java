package info.preva1l.fadah.watcher;

import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.records.Listing;
import lombok.Getter;
import lombok.experimental.UtilityClass;
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

    public void watch(@NotNull Player player, @NotNull Watching watching) {
        watchingListings.put(player.getUniqueId(), watching);
    }

    @Blocking
    public void alertWatchers(@NotNull Listing listing) {
        for (Map.Entry<UUID, Watching> entry : watchingListings.entrySet()) {
            Watching watching = entry.getValue();
            if (watching.search() != null) {
                if (checkForStringInItem(watching.search().toUpperCase(), listing.getItemStack())
                        || checkForEnchantmentOnBook(watching.search().toUpperCase(), listing.getItemStack())) {
                    sendAlert(entry.getKey(), listing);
                }
            }

            if (watching.maxPrice() == null || watching.minPrice() == null) return;

            if (watching.minPrice() < listing.getPrice() && watching.maxPrice() < listing.getPrice()) {
                sendAlert(entry.getKey(), listing);
            }
        }
    }

    private void sendAlert(UUID player, Listing listing) {
        // ceebs
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
