package info.preva1l.fadah.warnings;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created on 1/07/2025
 *
 * @author Preva1l
 */
public final class SpigotWarning extends JavaPlugin {
    private static final List<String> warning = List.of(
            "---------------- WARNING ----------------",
            "  Fadah does not support Spigot/Bukkit!  ",
            "    You must use one of the following:   ",
            "    Paper, Pufferfish, Purpur, USpigot   ",
            "     ASPaper, Folia or ShreddedPaper     ",
            "     Other Paper forks may also work     ",
            "-----------------------------------------",
            "        Fadah will now terminate!        ",
            "---------------- WARNING ----------------"
    );

    private final Logger logger = getLogger();

    @Override
    public void onLoad() {
        printWarning();
    }

    @Override
    public void onEnable() {
        printWarning();
    }

    private void printWarning() {
        warning.forEach(logger::severe);
    }
}
