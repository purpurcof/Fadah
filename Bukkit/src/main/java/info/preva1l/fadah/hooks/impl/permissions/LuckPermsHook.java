package info.preva1l.fadah.hooks.impl.permissions;

import info.preva1l.hooker.annotation.Hook;
import info.preva1l.hooker.annotation.OnStart;
import info.preva1l.hooker.annotation.Require;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.PermissionHolder;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Optional;

/**
 * Created on 27/03/2025
 *
 * @author Preva1l
 */
@Hook(id = "luckperms")
@Require("LuckPerms")
public class LuckPermsHook extends PermissionsHook {
    private LuckPerms luckPerms;

    /**
     * Sets the permission retriever to the luckperms implementation
     * <p>
     *      The luckperms implementation first sorts by group weight, if the user is in no groups, it just uses the users base permissions
     * </p>
     *
     * @return true if luckperms api was successful
     */
    @OnStart
    public boolean onStart() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) return false;
        luckPerms = provider.getProvider();

        setRetriever((perm, player) -> {
            String nodePrefix = perm.nodePrefix;
            QueryOptions queryOptions = QueryOptions.defaultContextualOptions()
                    .toBuilder()
                    .context(ImmutableContextSet.of("world", player.getWorld().getName()))
                    .build();

            return getLPUser(player)
                    .flatMap(user -> user.getInheritedGroups(queryOptions)
                            .stream()
                            .max(Comparator.comparingInt(g -> g.getWeight().orElse(0)))
                            .map(g -> (PermissionHolder) g)
                            .or(() -> Optional.of(user))
                            .flatMap(holder -> holder.getNodes()
                                    .stream()
                                    .filter(Node::getValue)
                                    .filter(n -> n.getKey().startsWith(nodePrefix))
                                    .filter(p -> canParse(p, nodePrefix))
                                    .map(p -> Integer.parseInt(p.getKey().substring(nodePrefix.length())))
                                    .max(perm.findHighest ? Comparator.naturalOrder() : Comparator.reverseOrder())
                            )
                    ).orElseGet(perm.defaultValue::intValue);
        });

        return true;
    }

    private Optional<User> getLPUser(Player player) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(player.getUniqueId()));
    }

    private boolean canParse(@NotNull Node permission, @NotNull String nodePrefix) {
        try {
            Integer.parseInt(permission.getKey().substring(nodePrefix.length()));
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
