package info.preva1l.fadah.utils.serialization;

import com.google.gson.*;
import info.preva1l.fadah.records.collection.CollectionBox;
import info.preva1l.fadah.records.collection.ExpiredItems;
import info.preva1l.fadah.records.history.History;
import info.preva1l.fadah.records.listing.Listing;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created on 25/02/2025
 *
 * @author Preva1l
 */
public class GsonCodec extends BaseCodec {
    private static final Logger LOGGER = Logger.getLogger(GsonCodec.class.getName());
    private Gson gson;
    private final Gson baseGson;
    private final Map<String, Class<?>> classMap = new ConcurrentHashMap<>();

    public GsonCodec() {
        this.baseGson = GsonComponentSerializer.gson()
                .populator()
                .apply(new GsonBuilder()
                        .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackTypeAdapter())
                )
                .create();

        this.gson = GsonComponentSerializer.gson()
                .populator()
                .apply(new GsonBuilder()
                        .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackTypeAdapter())
                        .registerTypeHierarchyAdapter(Listing.class, new ListingAdapter())
                        .registerTypeHierarchyAdapter(CollectionBox.class, new CollectableAdapter())
                        .registerTypeHierarchyAdapter(ExpiredItems.class, new ExpiredAdapter())
                        .registerTypeHierarchyAdapter(History.class, new HistoryAdapter())
                )
                .create();
    }

    private final Encoder encoder = in -> {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        try (ByteBufOutputStream os = new ByteBufOutputStream(out)) {
            Class<?> clazz = in.getClass();

            try {
                String json = gson.toJson(in, clazz);
                byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
                byte[] typeBytes = clazz.getName().getBytes(StandardCharsets.UTF_8);

                os.writeInt(jsonBytes.length);
                os.write(jsonBytes);

                os.writeInt(typeBytes.length);
                os.write(typeBytes);

                return os.buffer();
            } catch (Exception e) {
                LOGGER.severe("Failed to encode object of type " + clazz.getName() + ": " + e.getMessage());
                throw e;
            }
        } catch (IOException e) {
            out.release();
            throw e;
        } catch (Exception e) {
            out.release();
            throw new IOException("Encoding failed", e);
        }
    };

    private final Decoder<Object> decoder = (buf, state) -> {
        try (ByteBufInputStream is = new ByteBufInputStream(buf)) {
            int jsonLen = is.readInt();
            byte[] jsonBytes = new byte[jsonLen];
            is.readFully(jsonBytes);
            String json = new String(jsonBytes, StandardCharsets.UTF_8);

            int typeLen = is.readInt();
            byte[] typeBytes = new byte[typeLen];
            is.readFully(typeBytes);
            String type = new String(typeBytes, StandardCharsets.UTF_8);

            try {
                Class<?> clazz = getClassFromType(type);
                return gson.fromJson(json, clazz);
            } catch (Exception e) {
                LOGGER.severe("Failed to decode object of type " + type + " from JSON: " + json + ". Error: " + e.getMessage());
                throw e;
            }
        }
    };

    public Class<?> getClassFromType(String name) {
        Class<?> clazz = classMap.get(name);
        if (clazz == null) {
            try {
                clazz = Class.forName(name);
                classMap.put(name, clazz);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not find class named " + name, e);
            }
        }
        return clazz;
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }

    @Override
    public ClassLoader getClassLoader() {
        if (gson.getClass().getClassLoader() != null) {
            return gson.getClass().getClassLoader();
        }
        return super.getClassLoader();
    }

    class ListingAdapter implements JsonSerializer<Listing>, JsonDeserializer<Listing> {
        private static final String TYPE_FIELD = "type";

        @Override
        public JsonElement serialize(Listing src, Type typeOfSrc, JsonSerializationContext context) {
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(TYPE_FIELD, src.getClass().getName());

                JsonElement serializedObject = baseGson.toJsonTree(src, src.getClass());
                if (serializedObject.isJsonObject()) {
                    JsonObject srcObject = serializedObject.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : srcObject.entrySet()) {
                        jsonObject.add(entry.getKey(), entry.getValue());
                    }
                }

                return jsonObject;
            } catch (Exception e) {
                LOGGER.severe("Failed to serialize Listing: " + e.getMessage());
                throw new JsonParseException("Failed to serialize Listing", e);
            }
        }

        @Override
        public Listing deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                JsonObject jsonObject = json.getAsJsonObject();
                String type = jsonObject.get(TYPE_FIELD).getAsString();
                jsonObject.remove(TYPE_FIELD);

                Class<? extends Listing> clazz;
                try {
                    clazz = (Class<? extends Listing>) Class.forName(type);
                } catch (ClassNotFoundException | ClassCastException e) {
                    throw new JsonParseException("Could not find class for type: " + type, e);
                }

                return baseGson.fromJson(jsonObject, clazz);
            } catch (Exception e) {
                LOGGER.severe("Failed to deserialize Listing: " + e.getMessage());
                throw new JsonParseException("Failed to deserialize Listing", e);
            }
        }
    }

    class ExpiredAdapter implements JsonSerializer<ExpiredItems>, JsonDeserializer<ExpiredItems> {
        private static final String TYPE_FIELD = "type";

        @Override
        public JsonElement serialize(ExpiredItems src, Type typeOfSrc, JsonSerializationContext context) {
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(TYPE_FIELD, src.getClass().getName());

                JsonElement serializedObject = baseGson.toJsonTree(src, src.getClass());
                if (serializedObject.isJsonObject()) {
                    JsonObject srcObject = serializedObject.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : srcObject.entrySet()) {
                        jsonObject.add(entry.getKey(), entry.getValue());
                    }
                }

                return jsonObject;
            } catch (Exception e) {
                LOGGER.severe("Failed to serialize StorageHolder: " + e.getMessage());
                throw new JsonParseException("Failed to serialize StorageHolder", e);
            }
        }

        @Override
        public ExpiredItems deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                JsonObject jsonObject = json.getAsJsonObject();
                String type = jsonObject.get(TYPE_FIELD).getAsString();
                jsonObject.remove(TYPE_FIELD);

                Class<ExpiredItems> clazz;
                try {
                    clazz = (Class<ExpiredItems>) Class.forName(type);
                } catch (ClassNotFoundException | ClassCastException e) {
                    throw new JsonParseException("Could not find class for type: " + type, e);
                }

                return baseGson.fromJson(jsonObject, clazz);
            } catch (Exception e) {
                LOGGER.severe("Failed to deserialize StorageHolder: " + e.getMessage());
                throw new JsonParseException("Failed to deserialize StorageHolder", e);
            }
        }
    }

    class CollectableAdapter implements JsonSerializer<CollectionBox>, JsonDeserializer<CollectionBox> {
        private static final String TYPE_FIELD = "type";

        @Override
        public JsonElement serialize(CollectionBox src, Type typeOfSrc, JsonSerializationContext context) {
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(TYPE_FIELD, src.getClass().getName());

                JsonElement serializedObject = baseGson.toJsonTree(src, src.getClass());
                if (serializedObject.isJsonObject()) {
                    JsonObject srcObject = serializedObject.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : srcObject.entrySet()) {
                        jsonObject.add(entry.getKey(), entry.getValue());
                    }
                }

                return jsonObject;
            } catch (Exception e) {
                LOGGER.severe("Failed to serialize StorageHolder: " + e.getMessage());
                throw new JsonParseException("Failed to serialize StorageHolder", e);
            }
        }

        @Override
        public CollectionBox deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                JsonObject jsonObject = json.getAsJsonObject();
                String type = jsonObject.get(TYPE_FIELD).getAsString();
                jsonObject.remove(TYPE_FIELD);

                Class<CollectionBox> clazz;
                try {
                    clazz = (Class<CollectionBox>) Class.forName(type);
                } catch (ClassNotFoundException | ClassCastException e) {
                    throw new JsonParseException("Could not find class for type: " + type, e);
                }

                return baseGson.fromJson(jsonObject, clazz);
            } catch (Exception e) {
                LOGGER.severe("Failed to deserialize StorageHolder: " + e.getMessage());
                throw new JsonParseException("Failed to deserialize StorageHolder", e);
            }
        }
    }

    class HistoryAdapter implements JsonSerializer<History>, JsonDeserializer<History> {
        private static final String TYPE_FIELD = "type";

        @Override
        public JsonElement serialize(History src, Type typeOfSrc, JsonSerializationContext context) {
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(TYPE_FIELD, src.getClass().getName());

                JsonElement serializedObject = baseGson.toJsonTree(src, src.getClass());
                if (serializedObject.isJsonObject()) {
                    JsonObject srcObject = serializedObject.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : srcObject.entrySet()) {
                        jsonObject.add(entry.getKey(), entry.getValue());
                    }
                }

                return jsonObject;
            } catch (Exception e) {
                LOGGER.severe("Failed to serialize StorageHolder: " + e.getMessage());
                throw new JsonParseException("Failed to serialize StorageHolder", e);
            }
        }

        @Override
        public History deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                JsonObject jsonObject = json.getAsJsonObject();
                String type = jsonObject.get(TYPE_FIELD).getAsString();
                jsonObject.remove(TYPE_FIELD);

                Class<? extends History> clazz;
                try {
                    clazz = (Class<? extends History>) Class.forName(type);
                } catch (ClassNotFoundException | ClassCastException e) {
                    throw new JsonParseException("Could not find class for type: " + type, e);
                }

                return baseGson.fromJson(jsonObject, clazz);
            } catch (Exception e) {
                LOGGER.severe("Failed to deserialize StorageHolder: " + e.getMessage());
                throw new JsonParseException("Failed to deserialize StorageHolder", e);
            }
        }
    }
}