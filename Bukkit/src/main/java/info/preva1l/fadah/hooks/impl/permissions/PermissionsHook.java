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
    private static volatile BiFunction<Permission, Player, Number> valueRetriever = (perm, player) -> 0;

    static {
        setRetriever((perm, player) -> {
            String nodePrefix = perm.nodePrefix;

            return player.getEffectivePermissions()
                    .stream()
                    .filter(PermissionAttachmentInfo::getValue)
                    .filter(n -> n.getPermission().startsWith(nodePrefix))
                    .filter(p -> canParse(p, nodePrefix))
                    .map(p -> parseNumber(p.getPermission().substring(nodePrefix.length())))
                    .max(perm.findHighest ? compareNumbers() : compareNumbers().reversed())
                    .orElse(perm.defaultValue);
        });
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
            return type.cast(value.intValue());
        } else if (type == String.class) {
            return type.cast(value.toString());
        } else if (type == Number.class) {
            return type.cast(value);
        }
        throw new IllegalArgumentException("Unsupported return type: " + type.getSimpleName());
    }

    private static boolean canParse(@NotNull PermissionAttachmentInfo permission, @NotNull String nodePrefix) {
        try {
            parseNumber(permission.getPermission().substring(nodePrefix.length()));
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    protected static Number parseNumber(String str) {
        if (str.contains(".")) {
            return Double.parseDouble(str);
        } else {
            return Integer.parseInt(str);
        }
    }

    protected static void setRetriever(BiFunction<Permission, Player, Number> newRetriever) {
        valueRetriever = newRetriever;
    }

    protected static NumberComparator compareNumbers() {
        return NumberComparator.instance;
    }

    protected static class NumberComparator implements Comparator<Number> {
        static NumberComparator instance = new NumberComparator();

        @Override
        public int compare(Number n1, Number n2) {
            if (n1 == null && n2 == null) return 0;
            if (n1 == null) return -1;
            if (n2 == null) return 1;

            return Double.compare(n1.doubleValue(), n2.doubleValue());
        }
    }
}
