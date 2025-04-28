package info.preva1l.fadah.hooks.impl;

import com.gmail.nossr50.api.AbilityAPI;
import info.preva1l.fadah.commands.subcommands.SellSubCommand;
import info.preva1l.fadah.config.Lang;
import info.preva1l.hooker.annotation.Hook;
import info.preva1l.hooker.annotation.OnStart;
import info.preva1l.hooker.annotation.Require;

/**
 * Created on 28/04/2025
 *
 * @author Preva1l
 */
@Hook(id = "mcMMO")
@Require("mcMMO")
public class McMMOHook {
    @OnStart
    public void start() {
        SellSubCommand.restrictions.add(player -> {
            if (AbilityAPI.isAnyAbilityEnabled(player)) {
                Lang.sendMessage(player, Lang.i().getErrors().getMcmmoBlocking());
                return true;
            }

            return false;
        });
    }
}
