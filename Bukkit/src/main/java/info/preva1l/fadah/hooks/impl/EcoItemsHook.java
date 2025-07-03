package info.preva1l.fadah.hooks.impl;

import com.willfp.ecoitems.items.ItemUtilsKt;
import info.preva1l.fadah.filters.MatcherArgType;
import info.preva1l.fadah.filters.MatcherArgsRegistry;
import info.preva1l.hooker.annotation.Hook;
import info.preva1l.hooker.annotation.OnStart;
import info.preva1l.hooker.annotation.Require;

@Hook(id = "eco-items")
@Require("EcoItems")
@Require(type = "config", value = "eco-items")
public class EcoItemsHook {
    @OnStart
    public void onStart() {
        MatcherArgsRegistry.register(MatcherArgType.STRING, "ecoitems_id", item -> {
            var ecoitem = ItemUtilsKt.getEcoItem(item);
            if (ecoitem == null) return "";
            return ecoitem.getID();
        });
    }
}