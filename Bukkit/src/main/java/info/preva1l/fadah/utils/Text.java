package info.preva1l.fadah.utils;

import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.config.misc.Tuple;
import info.preva1l.fadah.hooks.impl.PapiHook;
import info.preva1l.hooker.Hooker;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * String Formatting Helper.
 */
@UtilityClass
public class Text {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();
    private final Pattern REMOVE_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-fA-F])|&[0-9a-fA-Fk-orK-OR]");

    /**
     * Takes a string formatted in minimessage OR legacy and turns it into an Adventure Component.
     *
     * @param message the modernMessage
     * @return colorized component
     */
    public Component modernMessage(@NotNull String message) {
        return miniMessage.deserialize(miniMessage.serialize(legacySerializer.deserialize("<!i>" + message)));
    }

    public Component modernMessage(@Nullable Player player, @NotNull String message) {
        Optional<PapiHook> hook = Hooker.getHook(PapiHook.class);
        if (hook.isPresent()) message = hook.get().format(player, message);
        return miniMessage.deserialize(miniMessage.serialize(legacySerializer.deserialize("<!i>" + message)));
    }

    public List<Component> modernList(@NotNull List<String> list) {
        return list.stream().map(Text::modernMessage).collect(Collectors.toList());
    }

    public List<Component> modernList(@Nullable Player player, List<String> list) {
        return list.stream().map(string -> Text.modernMessage(player, string)).collect(Collectors.toList());
    }

    /**
     * Strip color codes from a string, including hex codes, codes starting with the section symbol (§),
     * codes starting with an ampersand and minimessage codes.
     *
     * @param str String with color codes.
     * @return String without color codes.
     */
    public String removeColorCodes(String str) {
        str = legacySerializer.serialize(miniMessage.deserialize(str));
        str = REMOVE_PATTERN.matcher(str).replaceAll("");
        return str;
    }

    public double getAmountFromString(String priceString) throws NumberFormatException {
        if (priceString.toLowerCase().contains("nan") || priceString.toLowerCase().contains("infinity")) {
            throw new NumberFormatException();
        }

        double multi = 1;

        if (priceString.toLowerCase().endsWith("k")) {
            multi = 1000;
            priceString = priceString.replace("k", "");
            priceString = priceString.replace("K", "");
        } else if (priceString.toLowerCase().endsWith("m")) {
            multi = 1_000_000;
            priceString = priceString.replace("m", "");
            priceString = priceString.replace("M", "");
        } else if (priceString.toLowerCase().endsWith("b")) {
            multi = 1_000_000_000;
            priceString = priceString.replace("b", "");
            priceString = priceString.replace("B", "");
        } else if (priceString.toLowerCase().endsWith("t")) {
            multi = 1_000_000_000_000L;
            priceString = priceString.replace("t", "");
            priceString = priceString.replace("T", "");
        } else if (priceString.toLowerCase().endsWith("q")) {
            multi = 1_000_000_000_000_000L;
            priceString = priceString.replace("q", "");
            priceString = priceString.replace("Q", "");
        }

        return Double.parseDouble(priceString) * multi;
    }

    /**
     * Formats a message with placeholders.
     *
     * @param message message with placeholders
     * @param args    placeholders to replace
     * @return formatted string
     */
    @SafeVarargs
    public String replace(String message, Tuple<String, Object>... args) {
        for (Tuple<String, Object> replacement : args) {
            if (!message.contains(replacement.first)) continue;

            if (replacement.second instanceof Component comp) {
                message = message.replace(replacement.first, legacySerializer.serialize(comp));
                continue;
            }

            message = message.replace(replacement.first, String.valueOf(replacement.second));
        }
        return message;
    }

    /**
     * Formats a message with placeholders.
     *
     * @param message message with placeholders
     * @param args    placeholders to replace
     * @return formatted string
     */
    @SafeVarargs
    public Component replace(Component message, Tuple<String, Object>... args) {
        for (Tuple<String, Object> replacement : args) {
            TextComponent text = (TextComponent) message;
            if (!text.content().contains(replacement.first)) continue;

            if (replacement.second instanceof Component comp) {
                message = message.replaceText(conf -> conf.match(replacement.first).replacement(comp));
                continue;
            }

            message = message.replaceText(conf -> conf.match(replacement.first).replacement(String.valueOf(replacement.second)));
        }
        return message;
    }

    /**
     * Formats a list with placeholders.
     *
     * @param strings the list to format
     * @param args    placeholders to replace
     * @return formatted string
     */
    @SafeVarargs
    public List<String> replace(List<String> strings, Tuple<String, Object>... args) {
        List<String> result = new ArrayList<>(strings.size());
        strings.forEach(string -> result.add(replace(string, args)));
        return result;
    }

    /**
     * Capitalizes the first letter in a string.
     *
     * @param str String
     * @return Capitalized String
     */
    public String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Gets an item name from an item stack
     *
     * @param item item stack
     * @return formatted item name
     */
    public String extractItemName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Component display = meta.displayName();
            if (display != null) {
                return ((TextComponent) display).content();
            }

            if (meta.hasLocalizedName()) {
                return meta.getLocalizedName();
            }
        }
        String[] split = item.getType().name().toLowerCase().split("_");
        StringBuilder builder = new StringBuilder();
        for (String s : split) {
            builder.append(capitalizeFirst(s)).append(" ");
        }
        return builder.toString().trim();
    }

    /**
     * @return true if the item contains the search
     */
    public boolean doesItemHaveString(String toCheck, ItemStack item) {
        if (Config.i().getSearch().isType()) {
            if (item.getType().name().toUpperCase().contains(toCheck.toUpperCase())
                    || item.getType().name().toUpperCase().contains(toCheck.replace(" ", "_").toUpperCase())) {
                return true;
            }
        }

        if (item.getItemMeta() != null) {
            if (Config.i().getSearch().isName()) {
                Component display = item.getItemMeta().displayName();
                if (display != null && ((TextComponent) display).content().toUpperCase().contains(toCheck.toUpperCase())) {
                    return true;
                }
            }

            if (Config.i().getSearch().isLore()) {
                List<Component> lore = item.getItemMeta().lore();
                if (lore != null) {
                    for (Component l : lore) {
                        if (((TextComponent) l).content().toUpperCase().contains(toCheck.toUpperCase())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
