package info.preva1l.fadah.data.handler;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.data.dao.Dao;
import info.preva1l.fadah.data.dao.mongo.*;
import info.preva1l.fadah.data.fixers.v2.MongoFixerV2;
import info.preva1l.fadah.data.fixers.v2.V2Fixer;
import info.preva1l.fadah.data.fixers.v3.V3Fixer;
import info.preva1l.fadah.records.collection.CollectionBox;
import info.preva1l.fadah.records.collection.ExpiredItems;
import info.preva1l.fadah.records.history.History;
import info.preva1l.fadah.records.listing.Listing;
import info.preva1l.fadah.watcher.Watching;
import lombok.Getter;
import org.bson.UuidRepresentation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MongoHandler implements DatabaseHandler {
    private final Map<Class<?>, Dao<?>> daos = new HashMap<>();

    @Getter private boolean connected = false;

    private MongoClient client;
    private MongoDatabase database;
    @Getter private V2Fixer v2Fixer;
    @Getter private V3Fixer v3Fixer;

    @Override
    public void connect() {
        Config.Database conf = Config.i().getDatabase();
        try {
            @NotNull String connectionURI = conf.getUri();
            @NotNull String database = conf.getDatabase();
            Fadah.getConsole().info("Connecting to: " + connectionURI);
            final MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionURI))
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .build();

            this.client = MongoClients.create(settings);
            this.database = client.getDatabase(database);
            connected = true;
        } catch (Exception e) {
            destroy();
            throw new IllegalStateException("Failed to establish a connection to the MongoDB database. " +
                    "Please check the supplied database credentials in the config file", e);
        }

        registerDaos();
        v2Fixer = new MongoFixerV2();
        v3Fixer = V3Fixer.empty();
    }

    @Override
    public void destroy() {
        if (client != null) client.close();
    }

    @Override
    public void registerDaos() {
        daos.put(Listing.class, new ListingMongoDao(database));
        daos.put(CollectionBox.class, new CollectionBoxMongoDao(database));
        daos.put(ExpiredItems.class, new ExpiredItemsMongoDao(database));
        daos.put(History.class, new HistoryMongoDao(database));
        daos.put(Watching.class, new WatchersMongoDao(database));
    }

    @Override
    public <T> Dao<T> getDao(Class<?> clazz) {
        if (!daos.containsKey(clazz))
            throw new IllegalArgumentException("No DAO registered for class " + clazz.getName());
        return (Dao<T>) daos.get(clazz);
    }
}
