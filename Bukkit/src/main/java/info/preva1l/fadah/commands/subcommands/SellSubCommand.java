package info.preva1l.fadah.commands.subcommands;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.cache.CacheAccess;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.currency.CurrencyRegistry;
import info.preva1l.fadah.data.DatabaseManager;
import info.preva1l.fadah.guis.NewListingMenu;
import info.preva1l.fadah.hooks.impl.permissions.Permission;
import info.preva1l.fadah.hooks.impl.permissions.PermissionsHook;
import info.preva1l.fadah.records.listing.ImplListingBuilder;
import info.preva1l.fadah.records.listing.Listing;
import info.preva1l.fadah.records.post.PostResult;
import info.preva1l.fadah.utils.TaskManager;
import info.preva1l.fadah.utils.Text;
import info.preva1l.fadah.utils.commands.CommandArguments;
import info.preva1l.fadah.utils.commands.SubCommand;
import info.preva1l.fadah.utils.commands.SubCommandArgs;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SellSubCommand extends SubCommand {
    public static List<UUID> running = new ArrayList<>();

    public SellSubCommand(Fadah plugin) {
        super(plugin, Lang.i().getCommands().getSell().getAliases(), Lang.i().getCommands().getSell().getDescription());
    }

    public static void addRunning(UUID uuid) {
        running.add(uuid);
        TaskManager.Async.runLater(Fadah.getInstance(), () -> running.remove(uuid), 20 * 60L);
    }

    @SubCommandArgs(name = "sell", permission = "fadah.use")
    public void execute(@NotNull CommandArguments command) {
        if (!Config.i().isEnabled()) {
            command.reply(Lang.i().getPrefix() + Lang.i().getErrors().getDisabled());
            return;
        }
        assert command.getPlayer() != null;
        if (running.contains(command.getPlayer().getUniqueId())) return;
        addRunning(command.getPlayer().getUniqueId());
        if (command.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            command.reply(Lang.i().getPrefix() + Lang.i().getCommands().getSell().getMustHoldItem());
            running.remove(command.getPlayer().getUniqueId());
            return;
        }
        if (command.args().length == 0) {
            command.reply(Lang.i().getPrefix() + Lang.i().getErrors().getInvalidUsage()
                    .replace("%command%", Lang.i().getCommands().getSell().getUsage()));
            running.remove(command.getPlayer().getUniqueId());
            return;
        }
        String priceString = command.args()[0];

        double price;
        try {
            price = Text.getAmountFromString(priceString);
        } catch (NumberFormatException e) {
            command.reply(Lang.i().getPrefix() + Lang.i().getCommands().getSell().getMustBeNumber());
            running.remove(command.getPlayer().getUniqueId());
            return;
        }

        if (price < Config.i().getListingPrice().getMin()) {
            command.reply(Lang.i().getPrefix() + Lang.i().getCommands().getSell().getListingPrice().getMin()
                    .replace("%price%", Config.i().getListingPrice().getMin() + ""));
            running.remove(command.getPlayer().getUniqueId());
            return;
        }
        if (price > Config.i().getListingPrice().getMax()) {
            command.reply(Lang.i().getPrefix() + Lang.i().getCommands().getSell().getListingPrice().getMax()
                    .replace("%price%", Config.i().getListingPrice().getMax() + ""));
            running.remove(command.getPlayer().getUniqueId());
            return;
        }

        if (Config.i().isMinimalMode()) {
            handleSell(command, price);
        } else {
            new NewListingMenu(command.getPlayer(), price).open(command.getPlayer());
        }
    }

    private void handleSell(CommandArguments command, double price) {
        assert command.getPlayer() != null;
        Player player = command.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        new ImplListingBuilder(player)
                .currency(CurrencyRegistry.getAll().getFirst())
                .price(price)
                .tax(PermissionsHook.getValue(Double.class, Permission.LISTING_TAX, player))
                .itemStack(item)
                .length(Config.i().getDefaultListingLength().toMillis())
                .toPost()
                .buildAndSubmit().thenAcceptAsync(result -> TaskManager.Sync.run(plugin, player, () -> {
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
                }), DatabaseManager.getInstance().getThreadPool())
                .exceptionally(t -> {
                    Fadah.getConsole().log(Level.SEVERE, t.getMessage(), t);
                    running.remove(player.getUniqueId());
                    return null;
                });
    }
}
