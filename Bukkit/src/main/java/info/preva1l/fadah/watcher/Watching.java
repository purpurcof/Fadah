package info.preva1l.fadah.watcher;

import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record Watching(
        @Expose @NotNull UUID player,
        @Expose @Nullable String search,
        @Expose @Nullable Double minPrice,
        @Expose @Nullable Double maxPrice
) {
}
