package info.preva1l.fadah.hooks.impl.permissions;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.BiFunction;

/**
 * Created on 27/03/2025
 *
 * @author Preva1l
 */
public abstract class PermissionsHook {
    private static BiFunction<Permission, Player, Number> valueRetriever = (perm, player) -> 0;

    protected PermissionsHook() {
        setRetriever(defaultValueRetriever());
    }

    /**
     * Gets the number value from a permission.
     *
     * @param type       the number type to return as.
     * @param permission the permission to get.
     * @param player     the player to get for.
     * @return the overridden value in a permission, or the default.
     * @implNote  the default implementation gets the highest or lowest value, depending on the permission.
     */
    public static <T> T getValue(Class<T> type, Permission permission, Player player) {
        Number value = valueRetriever.apply(permission, player);

        if (type == Double.class) {
            return type.cast(value.doubleValue());
        } else if (type == Float.class) {
            return type.cast(value.floatValue());
        } else if (type == Integer.class) {
            return type.cast(value);
        } else if (type == String.class) {
            return type.cast(String.valueOf(value));
        }
        throw new IllegalArgumentException("Unsupported return type: " + type.getSimpleName());
    }

    public static void setRetriever(BiFunction<Permission, Player, Number> newRetriever) {
        valueRetriever = newRetriever;
    }

    private BiFunction<Permission, Player, Number> defaultValueRetriever() {
        return (perm, player) -> {
            String nodePrefix = perm.nodePrefix;

            return player.getEffectivePermissions()
                    .stream()
                    .filter(PermissionAttachmentInfo::getValue)
                    .filter(n -> n.getPermission().startsWith(nodePrefix))
                    .filter(p -> canParse(p, nodePrefix))
                    .map(p -> Integer.parseInt(p.getPermission().substring(nodePrefix.length())))
                    .max(perm.findHighest ? Comparator.naturalOrder() : Comparator.reverseOrder())
                    .orElseGet(perm.defaultValue::intValue);
        };
    }

    private boolean canParse(@NotNull PermissionAttachmentInfo permission, @NotNull String nodePrefix) {
        try {
            Integer.parseInt(permission.getPermission().substring(nodePrefix.length()));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
