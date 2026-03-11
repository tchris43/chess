package dataaccess;

import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createUser(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException {
        DataAccess db = getDataAccess(dbClass);

        String username = "taylor";
        var user = new UserData(username, "password", "taylor@gmail.com");
        assertDoesNotThrow(() -> db.createUser(username, user));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createUser(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException {
        DataAccess db = getDataAccess(dbClass);

        String username = "taylor";
        var user = new UserData(username, "password", "taylor@gmail.com");
        assertDoesNotThrow(() -> db.createUser(username, user));
    }

}
