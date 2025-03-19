package info.preva1l.fadah.data.dao.sql;

import com.zaxxer.hikari.HikariDataSource;
import info.preva1l.fadah.data.dao.common_sql.CommonSQLListingDao;

public class ListingSQLDao extends CommonSQLListingDao {
    public ListingSQLDao(HikariDataSource dataSource) {
        super(dataSource);
    }
}
