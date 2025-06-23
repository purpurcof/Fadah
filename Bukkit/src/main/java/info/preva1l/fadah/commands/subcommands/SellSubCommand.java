package info.preva1l.fadah.commands.subcommands;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.cache.CacheAccess;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.config.misc.Tuple;
import info.preva1l.fadah.currency.CurrencyRegistry;
import info.preva1l.fadah.guis.NewListingMenu;
import info.preva1l.fadah.hooks.impl.permissions.Permission;
import info.preva1l.fadah.hooks.impl.permissions.PermissionsHook;
import info.preva1l.fadah.records.listing.ImplListingBuilder;
import info.preva1l.fadah.records.listing.Listing;
import info.preva1l.fadah.records.post.PostResult;
import info.preva1l.fadah.utils.Tasks;
import info.preva1l.fadah.utils.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;

public final class SellSubCommand {
    private final Fadah plugin;
    public static List<UUID> running = new ArrayList<>();

    public static List<Predicate<Player>> restrictions = new ArrayList<>();

    public SellSubCommand(Fadah plugin) {
        this.plugin = plugin;
    }

    public void addRunning(UUID uuid) {
        running.add(uuid);
        Tasks.syncDelayed(plugin, () -> running.remove(uuid), 20 * 60L);
    }

    public void execute(Player player, double price) {
        if (running.contains(player.getUniqueId())) return;
        addRunning(player.getUniqueId());
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.sendMessage(Text.text(Lang.i().getPrefix() + Lang.i().getCommands().getSell().getMustHoldItem()));
            running.remove(player.getUniqueId());
            return;
        }

        if (price < Config.i().getListingPrice().getMin()) {
            player.sendMessage(Text.text(
                    Lang.i().getPrefix() + Lang.i().getCommands().getSell().getListingPrice().getMin(),
                    Tuple.of("%price%", Config.i().getListingPrice().getMin() + ""))
            );
            running.remove(player.getUniqueId());
            return;
        }

        if (price > Config.i().getListingPrice().getMax()) {
            player.sendMessage(Text.text(
                    Lang.i().getPrefix() + Lang.i().getCommands().getSell().getListingPrice().getMax(),
                    Tuple.of("%price%", Config.i().getListingPrice().getMax() + ""))
            );
            running.remove(player.getUniqueId());
            return;
        }

        for (Predicate<Player> restriction : restrictions) {
            if (restriction.test(player)) {
                return;
            }
        }

        if (Config.i().isMinimalMode()) {
            handleSell(player, price);
        } else {
             new NewListingMenu(player, price).open(player);
        }
    }

    private void handleSell(Player player, double price) {
        ItemStack item = player.getInventory().getItemInMainHand();
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        new ImplListingBuilder(player)
                .currency(CurrencyRegistry.getAll().getFirst())
                .price(price)
                .tax(PermissionsHook.getValue(Double.class, Permission.LISTING_TAX, player))
                .itemStack(item)
                .length(Config.i().getDefaultListingLength().toMillis())
                .toPost()
                .buildAndSubmit().thenAccept(result -> Tasks.sync(plugin, player, () -> {
                    if (result == PostResult.RESTRICTED_ITEM) {
                        player.getInventory().setItemInMainHand(item);
                        Lang.sendMessage(player, Lang.i().getPrefix() + Lang.i().getErrors().getRestricted());
                        running.remove(player.getUniqueId());
                        return;
                    }

                    if (result == PostResult.MAX_LISTINGS) {
                        player.getInventory().setItemInMainHand(item);
                        Lang.sendMessage(player, Lang.i().getPrefix() + Lang.i().getCommands().getSell().getMaxListings()
                                .replace("%max%", PermissionsHook.getValue(String.class, Permission.MAX_LISTINGS, player))
                                .replace("%current%", String.valueOf(CacheAccess.amountByPlayer(Listing.class, player.getUniqueId())))
                        );
                        running.remove(player.getUniqueId());
                        return;
                    }

                    if (!result.successful()) {
                        player.getInventory().setItemInMainHand(item);
                        Lang.sendMessage(player, Lang.i().getPrefix() + Lang.i().getErrors().getOther().replace("%ex%", result.message()));
                    }

                    running.remove(player.getUniqueId());
                }))
                .exceptionally(t -> {
                    plugin.getLogger().log(Level.SEVERE, t.getMessage(), t);
                    running.remove(player.getUniqueId());
                    return null;
                });
    }
}
