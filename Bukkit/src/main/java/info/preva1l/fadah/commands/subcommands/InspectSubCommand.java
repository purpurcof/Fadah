package info.preva1l.fadah.commands.subcommands;

import info.preva1l.fadah.data.DataService;
import info.preva1l.fadah.filters.SortingDirection;
import info.preva1l.fadah.filters.SortingMethod;
import info.preva1l.fadah.guis.ViewListingsMenu;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Created on 31/03/2025
 *
 * @author Preva1l
 */
public interface InspectSubCommand {
    default CompletableFuture<Void> inspect(
            Player player,
            OfflinePlayer owner,
            @Nullable String search,
            @Nullable SortingMethod sort,
            @Nullable SortingDirection direction) {
        return CompletableFuture.runAsync(() ->
                new ViewListingsMenu(
                        player,
                        owner,
                        search,
                        sort,
                        direction
                ).open(player), DataService.instance.getThreadPool()
        );
    }
}
