package info.preva1l.fadah.commands;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.commands.parsers.ColoringFormatter;
import info.preva1l.trashcan.extension.annotations.ExtensionReload;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import info.preva1l.trashcan.flavor.annotations.inject.Inject;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.logging.Logger;

@Service(priority = 2)
public final class CommandService {
    public static final CommandService instance = new CommandService();

    @Inject private Fadah plugin;
    @Inject private Logger logger;

    private LegacyPaperCommandManager<CommandSender> commandManager;
    private AnnotationParser<CommandSender> parser;

    private AuctionHouseCommand mainCommand;

    @Configure
    public void configure() {
        loadCommandManager();
        registerCommands();
    }

    @ExtensionReload
    public void reload() {
        mainCommand.reload();
    }

    private void registerCommands() {
        mainCommand = new AuctionHouseCommand(plugin, commandManager);
        parser.parse(new MigrateCommand());
    }

    private void loadCommandManager() {
        commandManager = LegacyPaperCommandManager.createNative(plugin, ExecutionCoordinator.simpleCoordinator());
        parser = new AnnotationParser<>(commandManager, CommandSender.class);

        MinecraftExceptionHandler.<CommandSender>createNative()
                .captionFormatter(new ColoringFormatter())
                .registerTo(commandManager);
    }
}
