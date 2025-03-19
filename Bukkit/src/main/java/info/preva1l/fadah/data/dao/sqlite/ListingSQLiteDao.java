package info.preva1l.fadah.data.dao.sqlite;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadah.data.dao.common_sql.CommonSQLListingDao;

public class ListingSQLiteDao extends CommonSQLListingDao {
    public ListingSQLiteDao(HikariDataSource dataSource) {
        super(dataSource);
    }
}
