package track.messenger.store;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class AbstractStore {

    DataSource connFactory;

    public AbstractStore() {
    }

    public AbstractStore(DataSource dataSource) {
        connFactory = dataSource;
    }
}
