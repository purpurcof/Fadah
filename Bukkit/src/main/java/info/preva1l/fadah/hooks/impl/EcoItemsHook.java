package info.preva1l.fadah.hooks.impl;

import com.willfp.ecoitems.items.ItemUtilsKt;
import info.preva1l.fadah.processor.ProcessorArgType;
import info.preva1l.fadah.processor.ProcessorArgsRegistry;
import info.preva1l.hooker.annotation.Hook;
import info.preva1l.hooker.annotation.OnStart;
import info.preva1l.hooker.annotation.Require;

@Hook(id = "eco-items")
@Require("EcoItems")
@Require(type = "config", value = "eco-items")
public class EcoItemsHook {
    @OnStart
    public void onStart() {
        ProcessorArgsRegistry.register(ProcessorArgType.STRING, "ecoitems_id", item -> {
            var ecoitem = ItemUtilsKt.getEcoItem(item);
            if (ecoitem == null) return "";
            return ecoitem.getID();
        });
    }
}