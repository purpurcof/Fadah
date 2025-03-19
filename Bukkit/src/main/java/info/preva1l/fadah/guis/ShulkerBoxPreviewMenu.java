package info.preva1l.fadah.guis;

import info.preva1l.fadah.records.listing.Listing;
import info.preva1l.fadah.utils.StringUtils;
import info.preva1l.fadah.utils.guis.*;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class ShulkerBoxPreviewMenu extends FastInv {
    public ShulkerBoxPreviewMenu(Listing listing, Player player,
                                 Runnable returnFunction) {
        super(36, StringUtils.extractItemName(listing.getItemStack()), LayoutManager.MenuType.SHULKER_PREVIEW);
        if (listing.getItemStack().getItemMeta() instanceof BlockStateMeta im) {
            if (im.getBlockState() instanceof ShulkerBox shulker) {
                for (int i = 0; i < shulker.getInventory().getSize(); i++) {
                    ItemStack itemStack = shulker.getInventory().getItem(i);
                    if (itemStack == null) {
                        itemStack = new ItemBuilder(Material.AIR).build();
                    }
                    setItem(i, itemStack);
                }
            }
        }

        setItem(31, GuiHelper.constructButton(GuiButtonType.CLOSE), e -> {
            returnFunction.run();
        });
        setItems(new int[]{27, 28, 29, 30, 32, 33, 34, 35}, GuiHelper.constructButton(GuiButtonType.BORDER));
    }
}
