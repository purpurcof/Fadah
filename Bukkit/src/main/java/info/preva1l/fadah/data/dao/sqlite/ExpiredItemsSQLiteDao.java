package info.preva1l.fadah.data.dao.sqlite;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadah.data.dao.common_sql.CommonSQLExpiredListingsDao;
import info.preva1l.fadah.records.collection.ExpiredItems;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class ExpiredItemsSQLiteDao extends CommonSQLExpiredListingsDao {
    private final HikariDataSource dataSource;

    /**
     * Save an object of type T to the database.
     *
     * @param collectableList the object to save.
     */
    @Override
    public void save(ExpiredItems collectableList) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO `expired_itemsV2`
                    (`playerUUID`,`items`)
                    VALUES (?,?)
                    ON CONFLICT(`playerUUID`) DO UPDATE SET
                        `items` = excluded.`items`;""")) {
                statement.setString(1, collectableList.owner().toString());
                statement.setString(2, GSON.toJson(collectableList.items(), EXPIRED_LIST_TYPE));
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            getLogger().severe("Failed to add item to expired items!");
            throw new RuntimeException(e);
        }
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
