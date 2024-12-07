package info.preva1l.fadah.watcher;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Watching {
    @Expose private final @NotNull UUID player;
    @Expose private @Nullable String search;
    @Expose private double minPrice;
    @Expose private double maxPrice;

    public static Watching base(Player player) {
        return new Watching(player.getUniqueId(), null, -1, -1);
    }
}
