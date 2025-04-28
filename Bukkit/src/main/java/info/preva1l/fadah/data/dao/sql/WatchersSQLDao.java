package info.preva1l.fadah.data.dao.sql;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadah.data.dao.common_sql.CommonWatchersSQLDao;
import info.preva1l.fadah.watcher.Watching;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

@RequiredArgsConstructor
public class WatchersSQLDao extends CommonWatchersSQLDao {
    private final HikariDataSource dataSource;

    /**
     * Save an object of type T to the database.
     *
     * @param watching the object to save.
     */
    @Override
    public void save(Watching watching) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO `watchers`
                         (`playerUUID`, `watching`)
                     VALUES (?, ?)
                     ON DUPLICATE KEY UPDATE
                         `watching` = VALUES(`watching`);""")) {
                statement.setString(1, watching.getPlayer().toString());
                statement.setString(2, GSON.toJson(watching));
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Failed to add item to watchers!", e);
        }
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
