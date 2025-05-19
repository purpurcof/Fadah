package info.preva1l.fadah.commands;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.cache.CacheAccess;
import info.preva1l.fadah.commands.parsers.PriceParser;
import info.preva1l.fadah.commands.subcommands.AboutSubCommand;
import info.preva1l.fadah.commands.subcommands.AdminSubCommands;
import info.preva1l.fadah.commands.subcommands.InspectSubCommand;
import info.preva1l.fadah.commands.subcommands.SellSubCommand;
import info.preva1l.fadah.config.Categories;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.data.DataService;
import info.preva1l.fadah.filters.SortingDirection;
import info.preva1l.fadah.filters.SortingMethod;
import info.preva1l.fadah.guis.*;
import info.preva1l.fadah.records.Category;
import info.preva1l.fadah.records.listing.BidListing;
import info.preva1l.fadah.records.listing.BinListing;
import info.preva1l.fadah.records.listing.Listing;
import info.preva1l.fadah.utils.TaskManager;
import info.preva1l.fadah.utils.Text;
import info.preva1l.fadah.utils.guis.LayoutService;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.minecraft.extras.AudienceProvider;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.incendo.cloud.bukkit.parser.OfflinePlayerParser.offlinePlayerParser;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static org.incendo.cloud.parser.standard.UUIDParser.uuidParser;

public class AuctionHouseCommand
        implements InspectSubCommand, AboutSubCommand, AdminSubCommands {
    private final SellSubCommand sellCommand;

    public AuctionHouseCommand(Fadah plugin, LegacyPaperCommandManager<CommandSender> manager) {
        this.sellCommand = new SellSubCommand(plugin);

        Lang.Commands conf = Lang.i().getCommands();

        MinecraftHelp<CommandSender> help = MinecraftHelp.<CommandSender>builder()
                .commandManager(manager)
                .audienceProvider(AudienceProvider.nativeAudience())
                .commandPrefix("/fadah help")
                .maxResultsPerPage(5)
                .colors(MinecraftHelp.helpColors(
                        TextColor.fromHexString(conf.getHelp().getPrimaryColor()),
                        TextColor.fromHexString(conf.getHelp().getHighlightColor()),
                        TextColor.fromHexString(conf.getHelp().getAlternativeHighlightColor()),
                        TextColor.fromHexString(conf.getHelp().getTextColor()),
                        TextColor.fromHexString(conf.getHelp().getAccentColor())
                ))
                .build();

        Command.Builder<CommandSender> builder = manager.commandBuilder(
                "fadah",
                Description.of(conf.getMain().getDescription()),
                conf.getMain().getAliases().toArray(String[]::new)
        );

        Stream.of(
                builder.senderType(Player.class)
                        .permission("fadah.use")
                        .futureHandler(cmd ->
                                CompletableFuture.runAsync(() ->
                                        new MainMenu(null, cmd.sender(), null, null, null)
                                                .open(cmd.sender()), DataService.instance.getThreadPool())),

                builder.literal("help",
                                Description.of(conf.getHelp().getDescription()),
                                conf.getHelp().getAliases().toArray(String[]::new))
                        .optional("query", greedyStringParser(), DefaultValue.constant(""))
                        .handler(context ->
                                help.queryCommands(context.get("query"), context.sender())),

                builder.literal("sell",
                                Description.of(conf.getSell().getDescription()),
                                conf.getSell().getAliases().toArray(String[]::new))
                        .senderType(Player.class)
                        .permission("fadah.use")
                        .required("price", PriceParser.create())
                        .handler(cmd -> sellCommand.execute(cmd.sender(), cmd.<Double>get("price"))),

                builder.literal("search",
                                Description.of(conf.getSearch().getDescription()),
                                conf.getSearch().getAliases().toArray(String[]::new))
                        .senderType(Player.class)
                        .permission("fadah.search")
                        .required("search", stringParser())
                        .flag(manager.flagBuilder("category").withAliases("c").withComponent(stringParser()).build())
                        .flag(manager.flagBuilder("sort-method").withAliases("s").withComponent(enumParser(SortingMethod.class)).build())
                        .flag(manager.flagBuilder("sort-direction").withAliases("d").withComponent(enumParser(SortingDirection.class)).build())
                        .flag(manager.flagBuilder("player").withAliases("p").withComponent(offlinePlayerParser()).build())
                        .futureHandler(cmd -> {
                            Category category = cmd.flags().<String>getValue("category")
                                    .flatMap(Categories::getCategory)
                                    .orElse(null);
                            SortingMethod sort = cmd.flags().<SortingMethod>getValue("sort-method").orElse(null);
                            SortingDirection direction = cmd.flags().<SortingDirection>getValue("sort-direction").orElse(null);
                            OfflinePlayer owner = cmd.flags().<OfflinePlayer>getValue("p").orElse(null);

                            if (owner != null) {
                                return inspect(cmd.sender(), owner, cmd.get("search"), sort, direction);
                            }

                            return CompletableFuture.runAsync(
                                    () -> new MainMenu(category, cmd.sender(), cmd.get("search"), sort, direction).open(cmd.sender()));
                        }),

                builder.literal("view",
                                Description.of(conf.getView().getDescription()),
                                conf.getView().getAliases().toArray(String[]::new))
                        .senderType(Player.class)
                        .permission("fadah.view")
                        .required("owner", offlinePlayerParser())
                        .futureHandler(cmd -> inspect(cmd.sender(), cmd.get("owner"), null, null, null)),

                builder.literal("view-listing",
                                Description.of(conf.getViewListing().getDescription()),
                                conf.getViewListing().getAliases().toArray(String[]::new))
                        .senderType(Player.class)
                        .permission("fadah.use")
                        .required("listingId", uuidParser())
                        .handler(cmd -> CacheAccess.get(Listing.class, cmd.get("listingId")).ifPresentOrElse(listing -> {
                                Player player = cmd.sender();
                                if (listing.isOwner(player)) {
                                    player.sendMessage(Text.text(Lang.i().getPrefix() + Lang.i().getErrors().getOwnListings()));
                                    return;
                                }

                                TaskManager.Async.run(Fadah.getInstance(), () -> {
                                    if (listing instanceof BinListing bin) {
                                        new ConfirmPurchaseMenu(bin, player, player::closeInventory).open(player);
                                    } else if (listing instanceof BidListing bid) {
                                        new PlaceBidMenu(bid, player, player::closeInventory).open(player);
                                    }
                                });
                            }, () -> cmd.sender().sendMessage(Text.text(Lang.i().getPrefix() + Lang.i().getErrors().getDoesNotExist())))
                        ),

                builder.literal("watch",
                                Description.of(conf.getWatch().getDescription()),
                                conf.getWatch().getAliases().toArray(String[]::new))
                        .senderType(Player.class)
                        .permission("fadah.watch")
                        .futureHandler(cmd -> CompletableFuture.runAsync(
                                () -> new WatchMenu(cmd.sender()).open(cmd.sender()))),

                builder.literal("active-listings",
                                Description.of(conf.getActiveListings().getDescription()),
                                conf.getActiveListings().getAliases().toArray(String[]::new))
                        .senderType(Player.class)
                        .permission("fadah.active-listings")
                        .optional("owner", offlinePlayerParser())
                        .futureHandler(cmd -> {
                            OfflinePlayer owner = cmd.getOrDefault("owner", null);
                            if (owner == null || !cmd.sender().hasPermission("fadah.manage.active-listings")) {
                                owner = cmd.sender();
                            }

                            return inspect(cmd.sender(), owner, null, null, null);
                        }),

                builder.literal("profile",
                                Description.of(conf.getProfile().getDescription()),
                                conf.getProfile().getAliases().toArray(String[]::new))
                        .senderType(Player.class)
                        .permission("fadah.profile")
                        .optional("owner", offlinePlayerParser())
                        .futureHandler(cmd -> CompletableFuture.runAsync(() -> {
                            OfflinePlayer owner = cmd.getOrDefault("owner", null);
                            if (owner == null || !cmd.sender().hasPermission("fadah.manage.profile")) {
                                owner = cmd.sender();
                            }

                            new ProfileMenu(cmd.sender(), owner).open(cmd.sender());
                        })),

                builder.literal("expired-items",
                                Description.of(conf.getExpiredItems().getDescription()),
                                conf.getExpiredItems().getAliases().toArray(String[]::new))
                        .senderType(Player.class)
                        .permission("fadah.expired-items")
                        .optional("owner", offlinePlayerParser())
                        .futureHandler(cmd -> CompletableFuture.runAsync(() -> {
                            OfflinePlayer owner = cmd.getOrDefault("owner", null);
                            if (owner == null || !cmd.sender().hasPermission("fadah.manage.expired-items")) {
                                owner = cmd.sender();
                            }

                            new CollectionMenu(cmd.sender(), owner, LayoutService.MenuType.EXPIRED_LISTINGS).open(cmd.sender());
                        })),

                builder.literal("collection-box",
                                Description.of(conf.getCollectionBox().getDescription()),
                                conf.getCollectionBox().getAliases().toArray(String[]::new))
                        .senderType(Player.class)
                        .permission("fadah.collection-box")
                        .optional("owner", offlinePlayerParser())
                        .futureHandler(cmd -> CompletableFuture.runAsync(() -> {
                            OfflinePlayer owner = cmd.getOrDefault("owner", null);
                            if (owner == null || !cmd.sender().hasPermission("fadah.manage.collection-box")) {
                                owner = cmd.sender();
                            }

                            new CollectionMenu(cmd.sender(), owner, LayoutService.MenuType.COLLECTION_BOX).open(cmd.sender());
                        })),


                builder.literal("history",
                                Description.of(conf.getHistory().getDescription()),
                                conf.getHistory().getAliases().toArray(String[]::new))
                        .senderType(Player.class)
                        .permission("fadah.use")
                        .optional("owner", offlinePlayerParser())
                        .futureHandler(cmd -> CompletableFuture.runAsync(() -> {
                            OfflinePlayer owner = cmd.getOrDefault("owner", null);
                            if (owner == null || !cmd.sender().hasPermission("fadah.manage.history")) {
                                owner = cmd.sender();
                            }

                            new HistoryMenu(cmd.sender(), owner, null).open(cmd.sender());
                        })),

                builder.literal("about",
                                Description.of(conf.getAbout().getDescription()),
                                conf.getAbout().getAliases().toArray(String[]::new))
                        .handler(cmd -> about(cmd.sender(), plugin)),

                builder.literal("toggle",
                                Description.of(conf.getToggle().getDescription()),
                                conf.getToggle().getAliases().toArray(String[]::new))
                        .permission("fadah.toggle-status")
                        .handler(cmd -> toggle(cmd.sender(), plugin)),

                builder.literal("reload",
                                Description.of(conf.getReload().getDescription()),
                                conf.getReload().getAliases().toArray(String[]::new))
                        .permission("fadah.reload")
                        .handler(cmd -> reload(cmd.sender(), plugin))

                ).forEach(manager::command);
    }
}
