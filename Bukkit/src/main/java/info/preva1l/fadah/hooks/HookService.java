package info.preva1l.fadah.hooks;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Config;
import info.preva1l.hooker.Hooker;
import info.preva1l.trashcan.plugin.annotations.PluginLoad;

public final class HookService {
    public static final HookService instance = new HookService();

    @PluginLoad
    public void loadHooks() {
        Hooker.register(
                Fadah.getInstance(),
                "info.preva1l.fadah.hooks.impl"
        );

        Hooker.requirement("config", value -> switch (value) {
                    case "discord" -> Config.i().getHooks().getDiscord().isEnabled();
                    case "eco-items" -> Config.i().getHooks().isEcoItems();
                    case "influxdb" -> Config.i().getHooks().getInfluxdb().isEnabled();
                    default -> true;
                }
        );
    }
}
