package info.preva1l.fadah.utils.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class ItemStackTypeAdapter extends TypeAdapter<ItemStack> {
    @Override
    public void write(JsonWriter out, ItemStack item) throws IOException {
        if (item == null) {
            out.nullValue();
            return;
        }
        String serialized = ItemSerializer.serialize(item);
        out.value(serialized);
    }

    @Override
    public ItemStack read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String data = in.nextString();
        ItemStack[] deserialized = ItemSerializer.deserialize(data);
        return deserialized.length > 0 ? deserialized[0] : null;
    }
}