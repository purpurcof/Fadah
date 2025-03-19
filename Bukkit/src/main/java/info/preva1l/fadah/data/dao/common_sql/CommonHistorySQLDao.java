package info.preva1l.fadah.data.dao.common_sql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.data.dao.Dao;
import info.preva1l.fadah.data.gson.BukkitSerializableAdapter;
import info.preva1l.fadah.records.history.HistoricItem;
import info.preva1l.fadah.records.history.History;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created on 19/03/2025
 *
 * @author Preva1l
 */
public abstract class CommonHistorySQLDao implements Dao<History> {
    protected static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new BukkitSerializableAdapter())
            .serializeNulls().disableHtmlEscaping().create();
    protected static final Type HISTORY_LIST_TYPE = new TypeToken<ArrayList<HistoricItem>>(){}.getType();

    /**
     * Get an object from the database by its id.
     *
     * @param id the id of the object to get.
     * @return an optional containing the object if it exists, or an empty optional if it does not.
     */
    @Override
    public Optional<History> get(UUID id) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                        SELECT `items`
                        FROM `historyV2`
                        WHERE `playerUUID`=?;""")) {
                statement.setString(1, id.toString());
                final ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    List<HistoricItem> items = GSON.fromJson(resultSet.getString("items"), HISTORY_LIST_TYPE);
                    return Optional.of(new History(id, items));
                }
            }
        } catch (SQLException e) {
            Fadah.getConsole().log(Level.SEVERE, "Failed to get item from history!", e);
        }
        return Optional.empty();
    }

    /**
     * Get all objects of type T from the database.
     *
     * @return a list of all objects of type T in the database.
     */
    @Override
    public List<History> getAll() {
        throw new NotImplementedException();
    }

    /**
     * Update an object of type T in the database.
     *
     * @param history the object to update.
     * @param params          the parameters to update the object with.
     */
    @Override
    public void update(History history, String[] params) {
        throw new NotImplementedException();
    }

    /**
     * Delete an object of type T from the database.
     *
     * @param collectableItem the object to delete.
     */
    @Override
    public void delete(History collectableItem) {
        throw new NotImplementedException();
    }

    protected abstract Connection getConnection() throws SQLException;
}
