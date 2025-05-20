package info.preva1l.fadah.commands.subcommands;

import info.preva1l.trashcan.chat.AboutMenu;
import info.preva1l.trashcan.extension.BaseExtension;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.context.CommandContext;

/**
 * Created on 31/03/2025
 *
 * @author Preva1l
 */
public interface AboutSubCommand {
    default void about(CommandContext<CommandSender> ctx) {
        final AboutMenu aboutMenu = AboutMenu.builder()
                .title(Component.text("Finally a Decent Auction House"))
                .description(Component.text("Fadah is the fast, modern and advanced auction house plugin that you have been looking for!"))
                .credits("Author",
                        AboutMenu.Credit.of("Preva1l")
                                .description("Click to visit website").url("https://docs.preva1l.info/"))
                .credits("Contributors",
                        AboutMenu.Credit.of("WuzzyLV"),
                        AboutMenu.Credit.of("asdevjava"),
                        AboutMenu.Credit.of("InvadedLands")
                )
                .buttons(
                        AboutMenu.Link.of("https://discord.gg/4KcF7S94HF").text("Discord Support").icon("⭐"),
                        AboutMenu.Link.of("https://docs.preva1l.info/fadlc/").text("Documentation").icon("📖")
                )
                .version(BaseExtension.instance().getCurrentVersion())
                .themeColor(TextColor.fromHexString("#9555FF"))
                .secondaryColor(TextColor.fromHexString("#bba4e0"))
                .build();
        ctx.sender().sendMessage(aboutMenu.toComponent());
    }
}
