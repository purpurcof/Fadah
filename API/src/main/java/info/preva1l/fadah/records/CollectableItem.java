package info.preva1l.fadah.records;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public record CollectableItem(
        ItemStack itemStack,
        long dateAdded
) {
    @Override
    public boolean equals(Object o) {
        if (o instanceof  CollectableItem collectableItem) {
            return collectableItem.dateAdded == this.dateAdded() && collectableItem.itemStack.equals(this.itemStack);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStack, dateAdded);
    }
}
