package info.preva1l.fadah.hooks;

import com.github.puregero.multilib.MultiLib;
import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.utils.Tasks;
import info.preva1l.hooker.Hooker;
import info.preva1l.hooker.HookerOptions;
import info.preva1l.trashcan.extension.annotations.PluginLoad;

public final class HookService {
    public static final HookService instance = new HookService();

    @PluginLoad
    public void loadHooks() {
        Hooker.register(
                Fadah.class,
                new HookerOptions(
                        Fadah.instance.getLogger(),
                        false,
                        runnable -> MultiLib.getAsyncScheduler().runNow(Fadah.instance, t -> runnable.run()),
                        runnable -> Tasks.sync(Fadah.instance, runnable),
                        runnable -> Tasks.syncDelayed(Fadah.instance, runnable, 60L),
                        "info.preva1l.fadah.hooks.impl"
                )
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
