package info.preva1l.fadah.guis;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.utils.TaskManager;
import info.preva1l.fadah.utils.Text;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.function.Consumer;

public class InputMenu<T> implements Listener {
    private final Class<T> type;
    private final Player player;

    public InputMenu(
            Player player,
            String title,
            String placeholder,
            Class<T> type,
            Consumer<T> callback
    ) {
        this.player = player;
        this.type = type;
        AnvilGUI.Builder guiBuilder = new AnvilGUI.Builder().plugin(Fadah.getInstance())
                .jsonTitle(JSONComponentSerializer.json().serialize(Text.text(title)));
        guiBuilder.text(placeholder);

        guiBuilder.onClick((slot, state) -> {
            if (slot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }

            return Collections.singletonList(AnvilGUI.ResponseAction.run(() ->
                    callback.accept(convertInput(state.getText(), placeholder))));
        });

        guiBuilder.onClose((state)-> {
            TaskManager.Sync.runLater(Fadah.getInstance(), () ->
                    callback.accept(convertInput(state.getText(), placeholder)),1L);
        });

        guiBuilder.open(player);
    }

    private T convertInput(String input, String placeholder) {
        if (input == null || input.equals(placeholder)) return null;

        try {
            if (type == Integer.class) {
                return (T) Integer.valueOf(input);
            } else if (type == Double.class) {
                return (T) Double.valueOf(Text.getAmountFromString(input));
            } else if (type == String.class) {
                return (T) input;
            } else {
                throw new IllegalArgumentException("Unsupported type: " + type.getName());
            }
        } catch (Exception e) {
            if (type.isAssignableFrom(Number.class)) {
                player.sendMessage(Text.text(Lang.i().getPrefix() + Lang.i().getCommands().getSell().getMustBeNumber()));
            }
            return null;
        }
    }
}
