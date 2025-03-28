package info.preva1l.fadah.config.misc;

import info.preva1l.fadah.utils.Text;
import info.preva1l.fadah.utils.guis.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created on 21/03/2025
 *
 * @author Preva1l
 */
public record ConfigurableItem(
        Material material,
        int modelData,
        String name,
        List<String> lore
) {
    public ItemStack itemStack() {
        return new ItemBuilder(material())
                .modelData(modelData())
                .name(Text.text(name()))
                .lore(Text.list(lore())).build();
    }
}
