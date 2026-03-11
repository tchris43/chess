package dataaccess;

import java.sql.SQLException;

public class DatabaseTests {
    private DataAccess getDataAccess(Class<? extends DataAccess> databaseClass) throws SQLException, DataAccessException{
        DataAccess db;
        if (databaseClass.equals(MySqlDataAccess.class)){
            db = new MySqlDataAccess();
        }
        else {
            db = new MemoryDataAccess();
        }
        return db;
    }
}
