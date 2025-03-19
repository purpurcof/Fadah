package info.preva1l.fadah.data.dao.sql;


import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.data.dao.common_sql.CommonHistorySQLDao;
import info.preva1l.fadah.records.history.History;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

@RequiredArgsConstructor
public class HistorySQLDao extends CommonHistorySQLDao {
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
                    INSERT INTO `historyV3`
                        (`playerUUID`, `items`)
                    VALUES (?, ?)
                    ON DUPLICATE KEY UPDATE
                        `items` = VALUES(`items`);""")) {
                statement.setString(1, history.owner().toString());
                statement.setString(2, GSON.toJson(history.historicItems(), HISTORY_LIST_TYPE));
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            Fadah.getConsole().log(Level.SEVERE, "Failed to add item to history!", e);
        }
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
