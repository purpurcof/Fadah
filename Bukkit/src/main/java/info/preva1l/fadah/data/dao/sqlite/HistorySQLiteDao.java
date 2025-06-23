package info.preva1l.fadah.data.dao.sqlite;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadah.data.dao.common_sql.CommonHistorySQLDao;
import info.preva1l.fadah.records.history.History;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

@RequiredArgsConstructor
public class HistorySQLiteDao extends CommonHistorySQLDao {
    private final HikariDataSource dataSource;

    /**
     * Save an object of type T to the database.
     *
     * @param history the object to save.
     */
    @Override
    public void save(History history) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO `historyV2`
                    (`playerUUID`,`items`)
                    VALUES (?,?)
                    ON CONFLICT(`playerUUID`) DO UPDATE SET
                        `items` = excluded.`items`;""")) {
                statement.setString(1, history.owner().toString());
                statement.setString(2, GSON.toJson(history.items(), HISTORY_LIST_TYPE));
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Failed to add item to history!", e);
        }
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
