package info.preva1l.fadah.commands;

import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.migrator.MigrationService;
import info.preva1l.fadah.utils.Text;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Permission;

import java.time.Instant;

@Command("fadah-migrate")
@Permission("fadah.migrate")
public class MigrateCommand {
    @Default
    public void execute(Player player, Plugin plugin) {
        MigrationService.instance.getMigrator(plugin.getName())
                .ifPresentOrElse(migrator -> {
                    long start = Instant.now().toEpochMilli();
                    player.sendMessage(Text.text(Lang.i().getPrefix() + "&fStarting migration from %s..."
                            .formatted(migrator.getMigratorName())));

                    migrator.startMigration().thenRun(() ->
                            player.sendMessage(Text.text(Lang.i().getPrefix() + "&aMigration from %s complete! &7(Took: %sms)"
                                    .formatted(migrator.getMigratorName(), Instant.now().toEpochMilli() - start))));
                }, () -> player.sendMessage(Text.text(Lang.i().getPrefix() + "&cMigrator does not exist!")));
    }
}
