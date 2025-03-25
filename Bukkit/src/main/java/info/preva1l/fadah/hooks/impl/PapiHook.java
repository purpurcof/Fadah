package info.preva1l.fadah.hooks.impl;

import info.preva1l.fadah.data.PermissionsData;
import info.preva1l.hooker.annotation.Hook;
import info.preva1l.hooker.annotation.OnStart;
import info.preva1l.hooker.annotation.Require;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Hook(id = "placeholders")
@Require("PlaceholderAPI")
public class PapiHook {
    @OnStart
    public boolean onEnable() {
        return new Expansion().register();
    }

    public String format(@Nullable final Player player, final String string) {
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    @Getter
    private static class Expansion extends PlaceholderExpansion {
        public final String identifier = "fadah";
        public final String author = "Preva1l";
        public final String version = "1.0.0";
        @Override public boolean persist() { return true; }

        @Override
        public String onRequest(OfflinePlayer p, @NotNull String params) {
            if (!(p instanceof Player player)) return null;
            return switch (params) {
                case "listings_current" -> String.valueOf(PermissionsData.getCurrentListings(player));
                case "listings_max" -> String.valueOf(PermissionsData.getHighestDouble(PermissionsData.PermissionType.MAX_LISTINGS, player));
                default -> null;
            };
        }
    }
}
