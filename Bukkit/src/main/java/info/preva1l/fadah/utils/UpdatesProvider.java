package info.preva1l.fadah.utils;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Config;
import net.william278.desertwell.util.UpdateChecker;
import net.william278.desertwell.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface UpdatesProvider {
    Fadah getPlugin();

    default void checkForUpdates() {
        final UpdateChecker checker = UpdateChecker.builder()
                .currentVersion(UpdatesHolder.self.pluginVersion)
                .endpoint(UpdateChecker.Endpoint.SPIGOT)
                .resource(Integer.toString(UpdatesHolder.SPIGOT_ID))
                .build();
        checker.check().thenAccept(checked -> UpdatesHolder.self.completed = checked);

        TaskManager.Sync.runLater(getPlugin(), () -> notifyUpdate(Bukkit.getConsoleSender()), 60L);
    }

    default void notifyUpdate(@NotNull CommandSender recipient) {
        if (!recipient.hasPermission("fadah.manage.profile")) return;
        var checked = UpdatesHolder.self.completed;
        if (checked.isUpToDate()) return;
        boolean critical = isCritical(checked);
        if (recipient instanceof Player && !Config.i().isUpdateChecker() && !critical) return;

        recipient.sendMessage(Text.modernMessage(
                "&7[Fadah]&f Fadah is &#D63C3COUTDATED&f! &7Current: &#D63C3C%s &7Latest: &#18D53A%s %s"
                        .formatted(checked.getCurrentVersion(),
                                checked.getLatestVersion(),
                                critical
                                        ? "\n&#D63C3C&lThis update is marked as critical. Update as soon as possible."
                                        : ""
                        )
        ));
    }

    private boolean isCritical(UpdateChecker.Completed checked) {
        return !checked.isUpToDate() &&
                (checked.getLatestVersion().getMetadata().equalsIgnoreCase("hotfix") ||
                        checked.getLatestVersion().getMajor() > checked.getCurrentVersion().getMajor() ||
                        checked.getLatestVersion().getMinor() > checked.getCurrentVersion().getMinor() + 5);
    }

    class UpdatesHolder {
        private static final UpdatesHolder self = new UpdatesHolder();
        private static final int SPIGOT_ID = 116157;

        private final Version pluginVersion = Version.fromString(Fadah.getInstance().getDescription().getVersion());
        private UpdateChecker.Completed completed;
    }
}
