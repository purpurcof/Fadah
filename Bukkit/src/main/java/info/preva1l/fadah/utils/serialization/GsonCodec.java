package info.preva1l.fadah.utils.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 25/02/2025
 *
 * @author Preva1l
 */
public class GsonCodec extends BaseCodec {
    private Gson gson;
    private final Map<String, Class<?>> classMap = new ConcurrentHashMap<>();

    public GsonCodec() {
        this.gson =
                GsonComponentSerializer.gson()
                        .populator()
                        .apply(new GsonBuilder().registerTypeHierarchyAdapter(ItemStack.class, new ItemStackTypeAdapter()))
                        .create();
    }

    private final Encoder encoder = in -> {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        try (ByteBufOutputStream os = new ByteBufOutputStream(out)) {
            byte[] jsonBytes = gson.toJson(in).getBytes(StandardCharsets.UTF_8);
            byte[] typeBytes = in.getClass().getName().getBytes(StandardCharsets.UTF_8);

            os.writeInt(jsonBytes.length);
            os.write(jsonBytes);

            os.writeInt(typeBytes.length);
            os.write(typeBytes);

            return os.buffer();
        } catch (IOException e) {
            out.release();
            throw e;
        } catch (Exception e) {
            out.release();
            throw new IOException(e);
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

            return gson.fromJson(json, getClassFromType(type));
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
}