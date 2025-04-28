package info.preva1l.fadah.utils;

import com.github.puregero.multilib.MultiLib;
import com.github.puregero.multilib.regionized.RegionizedTask;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

/**
 * Easy creation of Bukkit Tasks
 */
@SuppressWarnings("unused")
@UtilityClass
public class TaskManager {
    /**
     * Synchronous Tasks
     */
    @UtilityClass
    public class Sync {
        /**
         * Run a synchronous task once. Helpful when needing to run some sync code in an async loop
         *
         * @param plugin   The current plugin
         * @param runnable The runnable
         */
        public RegionizedTask run(Plugin plugin, Runnable runnable) {
            return MultiLib.getGlobalRegionScheduler().run(plugin,  t -> runnable.run());
        }

        /**
         * Run a synchronous task attached to an entities thread.
         * If it fails to get the entities thread it uses the global thread.
         *
         * @param plugin   The current plugin
         * @param runnable The runnable
         */
        public RegionizedTask run(Plugin plugin, Entity entity, Runnable runnable) {
            return MultiLib.getEntityScheduler(entity).run(plugin,  t -> runnable.run(), () -> run(plugin, runnable));
        }

        /**
         * Run a synchronous task forever with a delay between runs.
         *
         * @param plugin   The current plugin
         * @param runnable The runnable
         * @param interval Time between each run
         */
        public RegionizedTask runTask(Plugin plugin, Runnable runnable, long interval) {
            return MultiLib.getGlobalRegionScheduler().runAtFixedRate(plugin,  t -> runnable.run(), 0L, interval);
        }

        /**
         * Run a synchronous task once with a delay. Helpful when needing to run some sync code in an async loop
         *
         * @param plugin   The current plugin
         * @param runnable The runnable
         * @param delay    Time before running.
         */
        public RegionizedTask runLater(Plugin plugin, Runnable runnable, long delay) {
            return MultiLib.getGlobalRegionScheduler().runDelayed(plugin,  t -> runnable.run(), delay);
        }
    }

    /**
     * Asynchronous tasks
     */
    @UtilityClass
    public class Async {
        /**
         * Run an asynchronous task once. Helpful when needing to run some sync code in an async loop
         *
         * @param plugin   The current plugin
         * @param runnable The runnable
         */
        public RegionizedTask run(Plugin plugin, Runnable runnable) {
            return MultiLib.getAsyncScheduler().runNow(plugin, task -> runnable.run());
        }

        /**
         * Run an asynchronous task forever with a delay between runs.
         *
         * @param plugin   The current plugin
         * @param runnable The runnable
         * @param interval Time between each run
         */
        public RegionizedTask runTask(Plugin plugin, Runnable runnable, long delay, long interval) {
            return MultiLib.getAsyncScheduler().runAtFixedRate(plugin, task -> runnable.run(), delay, interval);
        }

        /**
         * Run an asynchronous task forever with a delay between runs.
         *
         * @param plugin   The current plugin
         * @param runnable The runnable
         * @param interval Time between each run
         */
        public RegionizedTask runTask(Plugin plugin, Runnable runnable, long interval) {
            return MultiLib.getAsyncScheduler().runAtFixedRate(plugin, task -> runnable.run(), 0L, interval);
        }

        /**
         * Run an asynchronous task once with a delay. Helpful when needing to run some sync code in an async loop
         *
         * @param plugin   The current plugin
         * @param runnable The runnable
         * @param delay    Time before running.
         */
        public RegionizedTask runLater(Plugin plugin, Runnable runnable, long delay) {
            return MultiLib.getAsyncScheduler().runDelayed(plugin, task -> runnable.run(), delay);
        }
    }
}