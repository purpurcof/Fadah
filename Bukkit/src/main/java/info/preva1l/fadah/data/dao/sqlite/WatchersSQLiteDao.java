package info.preva1l.fadah.data.dao.sqlite;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.data.dao.common_sql.CommonWatchersSQLDao;
import info.preva1l.fadah.watcher.Watching;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

@RequiredArgsConstructor
public class WatchersSQLiteDao extends CommonWatchersSQLDao {
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
                    (`playerUUID`,`watching`)
                    VALUES (?,?)
                    ON CONFLICT(`playerUUID`) DO UPDATE SET
                        `watching` = excluded.`watching`;""")) {
                statement.setString(1, watching.getPlayer().toString());
                statement.setString(2, GSON.toJson(watching));
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            Fadah.getConsole().log(Level.SEVERE, "Failed to add item to watchers!", e);
        }
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
