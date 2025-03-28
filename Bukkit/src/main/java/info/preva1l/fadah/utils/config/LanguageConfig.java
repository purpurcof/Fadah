package info.preva1l.fadah.utils.config;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.misc.Tuple;
import info.preva1l.fadah.utils.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LanguageConfig {
    private final ConfigurationSection superSection;

    public LanguageConfig(@NotNull ConfigurationSection superSection) {
        this.superSection = superSection;
    }

    public int getInt(String path, int def) {
        return superSection.getInt(path, def);
    }

    public int getInt(String path) {
        return superSection.getInt(path, 0);
    }

    public @NotNull Material getAsMaterial(String path) {
        Material material;
        String s = superSection.getString(path);
        if (s == null || s.isEmpty()) {
            throw new RuntimeException("No value at path %s".formatted(path));
        }
        try {
            material = Material.valueOf(s.toUpperCase());
        } catch (EnumConstantNotPresentException | IllegalArgumentException e) {
            material = Material.APPLE;
            Fadah.getConsole().severe("-----------------------------");
            Fadah.getConsole().severe("Config Incorrect!");
            Fadah.getConsole().severe("Material: " + s);
            Fadah.getConsole().severe("Does Not Exist!");
            Fadah.getConsole().severe("Defaulting to APPLE");
            Fadah.getConsole().severe("-----------------------------");
        }
        return material;
    }

    public @NotNull Material getAsMaterial(String path, Material def) {
        Material material;
        String s = superSection.getString(path);
        if (s == null || s.isEmpty()) {
            throw new RuntimeException("No value at path %s".formatted(path));
        }
        try {
            material = Material.valueOf(s.toUpperCase());
        } catch (EnumConstantNotPresentException | IllegalArgumentException e) {
            material = def;
            Fadah.getConsole().severe("-----------------------------");
            Fadah.getConsole().severe("Config Incorrect!");
            Fadah.getConsole().severe("Material: " + s);
            Fadah.getConsole().severe("Does Not Exist!");
            Fadah.getConsole().severe("Defaulting to " + def.toString());
            Fadah.getConsole().severe("-----------------------------");
        }
        return material;
    }

    public @NotNull String getString(String path, String def) {
        return superSection.getString(path, def);
    }

    public @NotNull Component getStringFormatted(String path) {
        return getStringFormatted(path, path);
    }

    @SafeVarargs
    public final @NotNull Component getStringFormatted(String path, String def, Tuple<String, Object>... replacements) {
        String f = superSection.getString(path);
        if (f == null || f.equals(path)) {
            return Component.text(def);
        }
        return Text.text(f, replacements);
    }

    public @NotNull List<Component> getLore(String path) {
        return getLore(null, path);
    }

    @SafeVarargs
    public final @NotNull List<Component> getLore(String path, Tuple<String, Object>... replacements) {
        return getLore(null, path, replacements);
    }

    @SafeVarargs
    public final @NotNull List<Component> getLore(Player player, String path, Tuple<String, Object>... replacements) {
        List<String> str = superSection.getStringList(path);
        if (str.isEmpty() || str.getFirst().equals(path) || str.getFirst().equals("null")) {
            return Collections.emptyList();
        }
        List<Component> ret = new ArrayList<>();
        for (String line : str) {
            ret.add(Text.text(player, line, replacements));
        }
        return ret;
    }
}
