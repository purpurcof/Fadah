package info.preva1l.fadah.records;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A category in the auction menu.
 * <br><br>
 * Created on 13/04/2024
 *
 * @author Preva1l
 * @param id the category id.
 * @param name the display name of the category.
 * @param priority the category's priority.
 * @param modelData the model data of the category icon.
 * @param icon the item to show in the main menu.
 * @param description the category description.
 * @param matchers the matchers to check items with.
 */
public record Category(
        @NotNull String id,
        @NotNull String name,
        int priority,
        int modelData,
        @NotNull Material icon,
        @NotNull List<String> description,
        @NotNull List<String> matchers
) implements Comparable<Category> {
    @Override
    public int compareTo(@NotNull Category o) {
        return Integer.compare(o.priority, this.priority);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id.equals(category.id);
    }
}