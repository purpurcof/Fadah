package info.preva1l.fadah.data.dao.sqlite;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.data.dao.common_sql.CommonCollectionBoxSQLDao;
import info.preva1l.fadah.records.collection.CollectionBox;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class CollectionBoxSQLiteDao extends CommonCollectionBoxSQLDao {
    private final HikariDataSource dataSource;

    /**
     * Save an object of type T to the database.
     *
     * @param collectableList the object to save.
     */
    @Override
    public void save(CollectionBox collectableList) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO collection_boxV2 (playerUUID, items)
                     VALUES (?, ?)
                     ON CONFLICT(playerUUID) DO UPDATE SET
                         items = excluded.items;""")) {
                statement.setString(1, collectableList.owner().toString());
                statement.setString(2, GSON.toJson(collectableList.collectableItems(), COLLECTION_LIST_TYPE));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Fadah.getConsole().severe("Failed to add item to collection box!");
            throw new RuntimeException(e);
        }
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
