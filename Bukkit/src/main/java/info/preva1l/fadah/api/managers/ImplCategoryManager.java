package info.preva1l.fadah.api.managers;

import info.preva1l.fadah.config.Categories;
import info.preva1l.fadah.records.Category;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created on 7/03/2025
 *
 * @author Preva1l
 */
public final class ImplCategoryManager implements CategoryManager {
    @Override
    public Optional<Category> get(@NotNull String id) {
        return Categories.getCategory(id);
    }

    @Override
    public String forItem(@NotNull ItemStack item) {
        return Categories.getCategoryForItem(item);
    }

    @Override
    public boolean register(@NotNull Category category) {
        return Categories.registerCategory(category);
    }

    @Override
    public boolean unregister(@NotNull String id) {
        return Categories.unregisterCategory(id);
    }
}
